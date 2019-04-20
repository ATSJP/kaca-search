package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.request.FileRequest;
import com.fast.kaca.search.web.response.FileResponse;
import com.fast.kaca.search.web.service.SearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author sjp
 * @date 2019/4/20
 **/
@Controller
@RequestMapping("/file")
public class FileController {
    @Resource
    private SearchService searchService;

    @ApiOperation(value = "下载文件", notes = "下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileId", value = "文件id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "isSource", value = "是否拿取原文件 0 否(获取查重后的文件) 1 是", required = true, dataType = "Number"),
    })
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> download(@Valid FileRequest request)
            throws IOException {
        FileResponse response = new FileResponse();
        searchService.download(request, response);
        if (ConstantApi.CODE.SUCCESS.getCode().equals(response.getCode())) {
            ByteArrayResource byteArrayResource = response.getByteArrayResource();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", response.getFileName()));
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentLength(byteArrayResource.contentLength())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(byteArrayResource);
        }
        return null;
    }

}
