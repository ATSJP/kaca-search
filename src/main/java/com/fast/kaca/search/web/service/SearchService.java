package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.dao.FileDao;
import com.fast.kaca.search.web.entity.FileEntity;
import com.fast.kaca.search.web.request.SearchRequest;
import com.fast.kaca.search.web.response.SearchResponse;
import com.fast.kaca.search.web.utils.FileUtils;
import com.fast.kaca.search.web.utils.LuceneTool;
import com.fast.kaca.search.web.utils.WordUtils;
import com.fast.kaca.search.web.vo.FileVo;
import com.fast.kaca.search.web.vo.SearchVo;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author sys
 * @date 2019/4/14
 **/
@Service
public class SearchService {

    private Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Resource
    private ConfigProperties configProperties;
    @Resource
    private LuceneTool luceneTool;
    @Resource
    private FileDao fileDao;

    /**
     * 跑所有文章的索引(仅首次没有任何索引时需要，其余增量索引)
     */
    public void initIndexTask(SearchRequest request) {
        logger.info("user start initIndexTask->uid:{}", request.getUid());
        // 单线程去跑数据
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("initUserEventWhenFinish-pool-%d")
                .build();
        ExecutorService singleThreadExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), namedThreadFactory);
        singleThreadExecutor.execute(this::createIndex);
    }

    /**
     * 生成索引
     * TODO 此方法待优化，一个文章一个线程，线程用线程池管理，参照 fork Join
     */
    public void createIndex() {
        // 获取所有要生成索引的文件名
        List<String> fileNameList = FileUtils.readFileContentList(configProperties.getFileDir());
        if (CollectionUtils.isEmpty(fileNameList)) {
            logger.info("there is nothing");
            return;
        }
        ArrayList<Document> documents = new ArrayList<>();
        fileNameList.forEach(item -> {
            logger.info("read article to create index start->fileName:{}", item);
            long start = System.currentTimeMillis();
            // 读取文件
            logger.info("readWordFile start->fileName:{}", item);
            long start1 = System.currentTimeMillis();
            List<String> paragraphList = WordUtils.readWordFile(configProperties.getFileDir() + item);
            long end1 = System.currentTimeMillis();
            logger.info("readWordFile end->fileName:{},time(ms):{}", item, (end1 - start1));
            if (CollectionUtils.isEmpty(paragraphList)) {
                logger.info("file is all of space or null->fileName:{}", item);
            } else {
                logger.info("create index start->fileName:{}", item);
                long start2 = System.currentTimeMillis();
                paragraphList.forEach(paragraph -> {
                    if (!StringUtils.isEmpty(paragraph)) {
                        Iterable<String> result = Splitter.fixedLength(configProperties.getTextLength()).trimResults()
                                .split(paragraph);
                        // 将文件分段，每指定配置字数为一段
                        for (String str : result) {
                            if (!StringUtils.isEmpty(str)) {
                                // 每段建立索引 并保存索引
                                Document doc = this.createDocument(item, str);
                                documents.add(doc);
                            }
                        }
                    }
                });
                long end2 = System.currentTimeMillis();
                logger.info("create index end->fileName:{},create index end(ms):{}", item, (end2 - start2));
            }
            long end = System.currentTimeMillis();
            logger.info("read article to create index end->fileName:{},size:{}", item, documents.size());
            logger.info("read article to create index end->fileName:{},timeSpend(ms):{}", item, (end - start));
        });
        try {
            luceneTool.write(documents, configProperties.getIndexDir());
        } catch (IOException e) {
            logger.error("write doc error:{}", e.getMessage());
        }
    }

    /**
     * 根据关键词创建
     *
     * @param articleName 文章的名称
     * @param text        文本
     * @return Document
     */
    private Document createDocument(String articleName, String text) {
        // 创建Document对象
        Document doc = new Document();
        // 获取每列数据
        // tips: StoredField 会储存，但不是被建立索引 StringField 会建立索引，但不会分词，TextField 会建立索引，也会分词
        Field articleNameField = new Field("articleName", articleName, TextField.TYPE_STORED);
        Field textField = new Field("text", text, TextField.TYPE_STORED);
        // 添加到Document中
        doc.add(articleNameField);
        doc.add(textField);
        // 返回doc
        return doc;
    }

    /**
     * 搜索
     *
     * @param request  req
     * @param response res
     * @throws Exception 异常
     */
    public void search(SearchRequest request, SearchResponse response) throws Exception {
        String key = request.getKey();
        IKAnalyzer ikAnalyzer = new IKAnalyzer();
        List<SearchVo> searchVoList = new LinkedList<>();
        // 索引目录对象
        Directory directory = FSDirectory.open(new File(configProperties.getIndexDir()));
        // 索引读取工具
        IndexReader reader = DirectoryReader.open(directory);
        // 索引搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);
        // 创建查询解析器,两个参数：默认要查询的字段的名称，分词器
        QueryParser parser = new QueryParser(Version.LUCENE_47, "text", ikAnalyzer);
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
        response.setData(searchVoList);
    }

    /**
     * 上传文件
     *
     * @param request  req
     * @param response res
     */
    public void upload(SearchRequest request, SearchResponse response) {
        // Get file name
        MultipartFile[] files = request.getFiles();
        String uploadedFileName = files == null ? "" : Arrays.stream(files).map(MultipartFile::getOriginalFilename)
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));
        if (StringUtils.isEmpty(uploadedFileName)) {
            response.setCode(ConstantApi.CODE.FAIL.getCode());
            response.setMsg(ConstantApi.FILE_UPLOAD.FAIL.getDesc());
            return;
        }
        Arrays.asList(files).forEach(item -> {
            if (!item.isEmpty()) {
                boolean isSaveFileSuccess = false;
                // 保存文件
                try {
                    byte[] bytes = item.getBytes();
                    Path path = Paths.get(configProperties.getFileDir() + item.getOriginalFilename());
                    Files.write(path, bytes);
                    isSaveFileSuccess = true;
                } catch (Exception e) {
                    logger.error("upload fail->e:{}", e.getMessage());
                }
                if (isSaveFileSuccess) {
                    // 保存上传文件记录
                    FileEntity fileEntity = new FileEntity();
                    fileEntity.setFileName(item.getOriginalFilename());
                    fileEntity.setCreateId(request.getUid());
                    fileEntity.setCreateTime(new Timestamp(System.currentTimeMillis()));
                    fileEntity.setUpdateId(request.getUid());
                    fileEntity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    fileDao.save(fileEntity);
                }
            }
        });
        // 建立索引
        this.initIndexTask(request);
    }

    public void fileList(SearchRequest request, SearchResponse response) {
        Short isListAll = request.getIsListAll();
        Short isListFileData = request.getIsListFileData();
        boolean isListAllTrue = ConstantApi.IS_TRUE.TRUE.getCode().equals(isListAll);
        boolean isListFileDataTrue = ConstantApi.IS_TRUE.TRUE.getCode().equals(isListFileData);
        if (isListAllTrue && isListFileDataTrue) {
            response.setCode(ConstantApi.CODE.ILLEGAL_REQUEST.getCode());
            response.setMsg(ConstantApi.CODE.ILLEGAL_REQUEST.getDesc());
            return;
        }
        if (isListFileDataTrue) {
            // 拿取库文件
            return;
        }
        if (isListAllTrue) {
            // 拿取所有用户的文件
            return;
        }
        // 拿取此用户的文件
        List<FileEntity> fileEntityList = fileDao.findAllByCreateId(request.getUid());
        if (CollectionUtils.isEmpty(fileEntityList)) {
            response.setCode(ConstantApi.CODE.SUCCESS.getCode());
            response.setMsg(ConstantApi.FILE_LIST.SUCCESS.getDesc());
            return;
        }
        List<FileVo> fileVoList = new LinkedList<>();
        fileEntityList.forEach(item -> {
            FileVo fileVo = new FileVo();
            BeanUtils.copyProperties(item, fileVo);
            fileVoList.add(fileVo);
        });
        response.setData(fileVoList);
    }

    public void download(SearchRequest request, SearchResponse response) {

    }
}
