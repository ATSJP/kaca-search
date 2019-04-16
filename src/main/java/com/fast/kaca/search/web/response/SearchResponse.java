package com.fast.kaca.search.web.response;

import com.fast.kaca.search.web.vo.SearchVo;

import java.util.List;

/**
 * @author sys
 * @date 2019/4/15
 **/
public class SearchResponse extends BaseResponse {
    private List<SearchVo> searchVoList;

    public List<SearchVo> getSearchVoList() {
        return searchVoList;
    }

    public void setSearchVoList(List<SearchVo> searchVoList) {
        this.searchVoList = searchVoList;
    }
}
