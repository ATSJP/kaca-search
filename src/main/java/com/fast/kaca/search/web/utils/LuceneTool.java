package com.fast.kaca.search.web.utils;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantSystem;
import com.fast.kaca.search.web.vo.SearchVo;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sys
 * @date 2019/4/14
 **/
@Component
public class LuceneTool {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigProperties configProperties;

    /**
     * 定义分词器
     */
    private static final Analyzer ANALYZER = new IKAnalyzer();

    /**
     * 封裝一个方法，用于将数据库中的数据解析为一个个关键字词存储到索引文件中
     *
     * @param docs docs
     */
    public void write(Collection<Document> docs, String path) throws IOException {
        //索引库的存储目录
        Directory directory = FSDirectory.open(new File(path));
        //关联当前lucene版本和分值器
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, ANALYZER);
        if (ConstantSystem.ENV.DEV.getDesc().equals(configProperties.getEnv())) {
            // 设置打开方式：OpenMode.APPEND 会在索引库的基础上追加新索引。OpenMode.CREATE会先清空原来数据，再提交新的索引
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        }
        //传入目录和分词器
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //写入到目录文件中
        indexWriter.addDocuments(docs);
        //提交事务
        indexWriter.commit();
        //关闭流
        indexWriter.close();
    }

    /**
     * 搜索
     *
     * @param key 关键词
     */
    public List<SearchVo> search(String key) {
        List<SearchVo> searchVoList = new LinkedList<>();
        try {
            IKAnalyzer ikAnalyzer = new IKAnalyzer();
            // 索引目录对象
            Directory directory = FSDirectory.open(new File(configProperties.getIndexDir()));
            // 索引读取工具
            IndexReader reader = DirectoryReader.open(directory);
            // 索引搜索工具
            IndexSearcher searcher = new IndexSearcher(reader);
            // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
            QueryParser parser = new QueryParser("text", ikAnalyzer);
            // 创建查询对象
            Query query = parser.parse(key);
            // 最终被分词后添加的前缀和后缀处理器，默认是粗体<B></B>
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<font color=red>", "</font>");
            // 高亮搜索的词添加到高亮处理器中
            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
            // 搜索数据,两个参数：查询条件对象要查询的最大结果条数
            // 返回的结果是 按照匹配度排名得分前N名的文档信息（包含查询到的总条数信息、所有符合条件的文档的编号信息）。
            TopDocs topDocs = searcher.search(query, 10);
            // 获取得分文档对象（ScoreDoc）数组.ScoreDoc中包含：文档的编号、文档的得分
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                // 取出文档编号
                int docID = scoreDoc.doc;
                // 根据编号去找文档
                Document doc = reader.document(docID);
                String text = doc.get("text");
                // 将查询的词和搜索词匹配，匹配到添加前缀和后缀
                TokenStream tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docID, "text", ikAnalyzer);
                // 传入的第二个参数是查询的值
                TextFragment[] frag = highlighter.getBestTextFragments(tokenStream, text, false, 10);
                String textValue = "";
                for (TextFragment textFragment : frag) {
                    if ((textFragment != null) && (textFragment.getScore() > 0)) {
                        textValue = ((textFragment.toString()));
                    }
                }
                SearchVo searchVo = new SearchVo();
                searchVo.setArticleName(doc.get("articleName"));
                searchVo.setText(textValue);
                searchVo.setScore(scoreDoc.score);
                searchVoList.add(searchVo);
            }
        } catch (Exception e) {
            logger.error("search error->e:{}", e.getMessage());
        }
        return searchVoList;
    }

}
