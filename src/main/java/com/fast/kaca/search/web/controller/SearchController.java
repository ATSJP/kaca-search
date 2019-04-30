package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.request.FileRequest;
import com.fast.kaca.search.web.request.SearchRequest;
import com.fast.kaca.search.web.response.FileResponse;
import com.fast.kaca.search.web.response.SearchResponse;
import com.fast.kaca.search.web.service.SearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * 搜索
 *
 * @author sys
 * @date 2019/4/5
 **/
@RestController
@RequestMapping(value = "/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    @ApiOperation(value = "搜索", notes = "搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "key", value = "搜索关键词", required = true, dataType = "String")
    })
    @GetMapping()
    public SearchResponse search(@Valid SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.search(request, response);
        return response;
    }

    @ApiOperation(value = "建立索引", notes = "建立索引")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "key", value = "文章题目", required = true, dataType = "String")
    })
    @GetMapping(value = "/createIndex")
    public SearchResponse createIndex(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.initIndexTask(request);
        return response;
    }

    @ApiOperation(value = "获取已上传的文件list", notes = "获取已上传的文件list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "isListType", value = "获取文件list: 0 拿自己的 1 获取库文件", required = true, dataType = "Number")
    })
    @GetMapping("/fileList")
    public SearchResponse fileList(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.fileList(request, response);
        return response;
    }

    @ApiOperation(value = "获取已上传的文件", notes = "获取已上传的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileId", value = "文件id", required = true, dataType = "Number")
    })
    @GetMapping("/getFile")
    public SearchResponse getFile(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.getFile(request, response);
        return response;
    }

    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file")
    })
    @PutMapping("/upload")
    public SearchResponse upload(SearchRequest request, MultipartFile[] files) {
        // TODO 为何映射不到复杂类型里 待解决
        SearchResponse response = new SearchResponse();
        request.setFiles(files);
        searchService.upload(request, response);
        return response;
    }

    @ApiOperation(value = "下载文件", notes = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileId", value = "文件id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "isSource", value = "是否拿取原文件 0 否(获取查重后的文件) 1 是", required = true, dataType = "Number"),
    })
    @GetMapping(value = "/download")
    public void download(@Valid FileRequest request, HttpServletResponse res) throws Exception {
        FileResponse response = new FileResponse();
        searchService.download(request, response);
        if (ConstantApi.CODE.SUCCESS.getCode().equals(response.getCode())) {
            ByteArrayOutputStream byteArrayOutputStream = response.getByteArrayOutputStream();
            if (byteArrayOutputStream != null) {
                byte[] data = byteArrayOutputStream.toByteArray();
                String fileName = new String(response.getFileName().getBytes("GBK"), StandardCharsets.ISO_8859_1);
                res.reset();
                res.setHeader("Content-Disposition",
                        "attachment; filename=\"" + fileName + ".zip\"");
                res.addHeader("Content-Length", "" + data.length);
                res.setContentType("application/octet-stream; charset=UTF-8");
                IOUtils.write(data, res.getOutputStream());
            }
        }
    }
}
