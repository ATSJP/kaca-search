package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.constant.ConstantSystem;
import com.fast.kaca.search.web.dao.FileDao;
import com.fast.kaca.search.web.dao.UserDao;
import com.fast.kaca.search.web.entity.FileEntity;
import com.fast.kaca.search.web.entity.UserEntity;
import com.fast.kaca.search.web.request.FileRequest;
import com.fast.kaca.search.web.request.SearchRequest;
import com.fast.kaca.search.web.response.FileResponse;
import com.fast.kaca.search.web.response.SearchResponse;
import com.fast.kaca.search.web.utils.FileUtils;
import com.fast.kaca.search.web.utils.LuceneTool;
import com.fast.kaca.search.web.utils.StringHelpUtils;
import com.fast.kaca.search.web.utils.WordUtils;
import com.fast.kaca.search.web.vo.FileVo;
import com.fast.kaca.search.web.vo.SearchVo;
import com.google.common.base.Splitter;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    private CheckRepeatService checkRepeatService;
    @Resource
    private LuceneTool luceneTool;
    @Resource
    private FileDao fileDao;
    @Resource
    private UserDao userDao;

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
        List<String> fileNameList = FileUtils.readFileContentList(configProperties.getFileSourceDir());
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
            List<String> paragraphList = WordUtils.readWordFile(configProperties.getFileSourceDir() + item);
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
//        Field textField = new StringField("text", text, Field.Store.YES);
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
     */
    public void search(SearchRequest request, SearchResponse response) {
        String key = request.getKey();
        List<SearchVo> searchVoList = luceneTool.search(key);
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
                    Path path = Paths.get(configProperties.getFileSourceDir() + item.getOriginalFilename());
                    Files.write(path, bytes);
                    isSaveFileSuccess = true;
                } catch (Exception e) {
                    logger.error("upload fail->Exception:{}", e);
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
                    // 论文查重-建立查重文件
                    checkRepeatService.startCheckRepeatTask(item.getOriginalFilename());
                }
            }
        });
        // 此篇文章入库,建立索引
        this.initIndexTask(request);
    }

    /**
     * 已上传文件list
     *
     * @param request  req
     * @param response res
     */
    public void fileList(SearchRequest request, SearchResponse response) {
        // 获取文件list: 0 拿自己的 1 获取库文件
        Short isListType = request.getIsListType();
        if (isListType == null) {
            response.setCode(ConstantApi.CODE.PARAM_NULL.getCode());
            response.setMsg(ConstantApi.CODE.PARAM_NULL.getDesc());
            return;
        }
        boolean isListAllTrue = ConstantApi.FILE_LIST_TYPE.ALL.getCode().equals(isListType);
        if (isListAllTrue) {
            // 拿取库文件
            List<FileVo> fileVoList = this.getAllUserFileList();
            this.assembleFileListResponse(response, fileVoList);
            return;
        }
        boolean isListSelfTrue = ConstantApi.FILE_LIST_TYPE.SELF.getCode().equals(isListType);
        if (isListSelfTrue) {
            // 拿取此用户的文件
            List<FileVo> fileVoList = this.getCurrentUserFileList(request.getUid());
            this.assembleFileListResponse(response, fileVoList);
            return;
        }
        response.setCode(ConstantApi.CODE.ILLEGAL_REQUEST.getCode());
        response.setMsg(ConstantApi.CODE.ILLEGAL_REQUEST.getDesc());
    }

    /**
     * 组装文件list
     *
     * @param response   res
     * @param fileVoList 文件list
     */
    private void assembleFileListResponse(SearchResponse response, List<FileVo> fileVoList) {
        if (CollectionUtils.isEmpty(fileVoList)) {
            response.setCode(ConstantApi.CODE.SUCCESS.getCode());
            response.setMsg(ConstantApi.FILE_LIST.SUCCESS.getDesc());
        }
        response.setData(fileVoList);
    }

    /**
     * 下载文件
     *
     * @param request  req
     * @param response res
     */
    public void download(FileRequest request, FileResponse response) {
        Integer fileId = request.getFileId();
        Short isSource = request.getIsSource();
        Optional<FileEntity> fileEntityOptional = fileDao.findById(fileId);
        if (fileEntityOptional.isPresent()) {
            String fileName = fileEntityOptional.get().getFileName();
            String filePath = this.getFilePathByFileNameAndIsSource(isSource, fileName);
            byte[] content;
            try {
                content = FileUtils.getContent(filePath);
            } catch (IOException e) {
                logger.info("get file content error->fileId:{},isSource:{},e:{}", fileId, isSource, e);
                response.setCode(ConstantApi.CODE.FAIL.getCode());
                response.setMsg(ConstantApi.FILE_DOWNLOAD.FAIL.getDesc());
                return;
            }
            if (content == null) {
                logger.info("get file error,byte is empty->fileId:{},isSource:{}", fileId, isSource);
                response.setCode(ConstantApi.CODE.FAIL.getCode());
                response.setMsg(ConstantApi.FILE_DOWNLOAD.FAIL.getDesc());
                return;
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream);
            try {
                zip.putNextEntry(new ZipEntry(fileName));
                IOUtils.write(content, zip);
            } catch (IOException e) {
                logger.info("get file error,byte is empty->fileId:{},isSource:{},e:{}", fileId, isSource, e);
            } finally {
                try {
                    zip.closeEntry();
                } catch (IOException e) {
                    logger.info("close zip error->fileId:{},isSource:{},e:{}", fileId, isSource, e);
                }
            }
            response.setByteArrayOutputStream(byteArrayOutputStream);
            response.setFileName(StringHelpUtils.removeSuffix(fileName));
        }
    }



    /**
     * 获取文件路径
     *
     * @param isSource 是否拿取原文件 0 否(获取查重后的文件) 1 是
     * @param fileName 文件全名
     * @return 文件路径
     */
    private String getFilePathByFileNameAndIsSource(Short isSource, String fileName) {
        String filePath;
        boolean isSourceTrue = ConstantApi.IS_TRUE.TRUE.getCode().equals(isSource);
        if (isSourceTrue) {
            // 下载源文件
            filePath = configProperties.getFileSourceDir() + fileName;
        } else {
            // 下载处理好的文件
            filePath = configProperties.getFileResultDir() + ConstantSystem.VERSION + fileName;
        }
        return filePath;
    }

    /**
     * 获取所有用户上传的文件
     *
     * @return fileList
     */
    private List<FileVo> getAllUserFileList() {
        Iterable<FileEntity> fileEntityList = fileDao.findAll();
        Iterator<FileEntity> iterable = fileEntityList.iterator();
        List<FileVo> fileVoList = new LinkedList<>();
        while (iterable.hasNext()) {
            FileVo fileVo = new FileVo();
            BeanUtils.copyProperties(iterable.next(), fileVo);
            fileVoList.add(fileVo);
        }
        return fileVoList;
    }

    /**
     * 根据用户id，获取用户上传的文件
     *
     * @param uid 用户id
     * @return 上传的文件
     */
    private List<FileVo> getCurrentUserFileList(Integer uid) {
        List<FileEntity> fileEntityList = fileDao.findAllByCreateId(uid);
        if (CollectionUtils.isEmpty(fileEntityList)) {
            return Collections.emptyList();
        }
        // TODO 此处可以优化为list copy方法
        List<FileVo> fileVoList = new LinkedList<>();
        fileEntityList.forEach(item -> {
            FileVo fileVo = new FileVo();
            BeanUtils.copyProperties(item, fileVo);
            Optional<UserEntity> userEntityOptional = userDao.findById(item.getCreateId());
            if (userEntityOptional.isPresent()) {
                UserEntity userEntity = userEntityOptional.get();
                String userName = userEntity.getUserName();
                fileVo.setCreateName(userName.substring(0, 1) + "***");
            }
            fileVoList.add(fileVo);
        });
        return fileVoList;
    }

}
