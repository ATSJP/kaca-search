package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantSystem;
import com.fast.kaca.search.web.utils.LuceneTool;
import com.fast.kaca.search.web.utils.WordUtils;
import com.fast.kaca.search.web.vo.SearchVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sys
 * @date 2019/4/20
 **/
@Service
public class CheckRepeatService {

    @Resource
    private ConfigProperties configProperties;
    @Resource
    private LuceneTool luceneTool;

    public void startCheckRepeatTask(String fileName) {
        // 已经分段
        List<String> paragraphList = WordUtils.readWordFile(configProperties.getFileSourceDir() + fileName);
        // 处理完毕
        List<String> newParagraphList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(paragraphList)) {
            paragraphList.forEach(item -> {
                List<SearchVo> searchVoList = luceneTool.search(item);
                if (!CollectionUtils.isEmpty(searchVoList)) {
                    // 此处加红
                    item = "<font color=red>" + item + "</font>";
                }
                newParagraphList.add(item);
            });
        }
        WordUtils.newWord(newParagraphList, configProperties.getFileResultDir() + ConstantSystem.VERSION + fileName);
    }
}
