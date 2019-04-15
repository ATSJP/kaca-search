package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.request.SearchRequest;
import com.fast.kaca.search.web.response.SearchResponse;
import com.fast.kaca.search.web.service.SearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author sys
 * @date 2019/4/5
 **/
@RestController
@RequestMapping(value = "/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    @ApiOperation(value = "上传文件", notes = "上传文件1")
    @ApiImplicitParam(name = "key", value = "上传文件内容", required = true, dataType = "String")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public SearchResponse search(@Valid SearchRequest request) {
        return new SearchResponse();
    }

    @ApiOperation(value = "获取查重结果", notes = "")
    @ApiImplicitParam(name = "key", value = "文章题目", required = true, dataType = "String")
    @RequestMapping(value = "/createIndex", method = RequestMethod.GET)
    public SearchResponse createIndex(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.createIndex();
        return response;
    }
}
