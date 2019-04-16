package com.fast.kaca.search.web.controller;

import com.fast.kaca.search.web.request.SearchRequest;
import com.fast.kaca.search.web.response.SearchResponse;
import com.fast.kaca.search.web.service.SearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

    @ApiOperation(value = "测试搜索", notes = "测试搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "key", value = "搜索关键词", required = true, dataType = "String")
    })
    @RequestMapping(method = RequestMethod.GET)
    public SearchResponse search(@Valid SearchRequest request) throws Exception {
        SearchResponse response = new SearchResponse();
        searchService.search(request, response);
        return response;
    }

    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "file", value = "文件", required = true, dataType = "file")
    })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public SearchResponse upload(@Valid SearchRequest request) {
        SearchResponse response = new SearchResponse();
        return response;
    }

    @ApiOperation(value = "获取查重结果", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "用户唯一id", required = true, dataType = "Number"),
            @ApiImplicitParam(name = "token", value = "token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "key", value = "文章题目", required = true, dataType = "String")
    })
    @RequestMapping(value = "/createIndex", method = RequestMethod.GET)
    public SearchResponse createIndex(SearchRequest request) {
        SearchResponse response = new SearchResponse();
        searchService.initIndexTask();
        return response;
    }
}
