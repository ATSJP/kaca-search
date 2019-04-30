package com.fast.kaca.search.web.service;

import com.fast.kaca.search.web.config.ConfigProperties;
import com.fast.kaca.search.web.constant.ConstantSystem;
import com.fast.kaca.search.web.dao.FileDao;
import com.fast.kaca.search.web.entity.FileEntity;
import com.fast.kaca.search.web.utils.LuceneTool;
import com.fast.kaca.search.web.utils.WordUtils;
import com.fast.kaca.search.web.vo.SearchVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sys
 * @date 2019/4/20
 **/
@Service
public class CheckRepeatService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ConfigProperties configProperties;
    @Resource
    private LuceneTool luceneTool;
    @Resource
    private FileDao fileDao;

    public void startCheckRepeatTask(String fileName) {
        // 已经分段
        List<String> paragraphList = WordUtils.readWordFile(configProperties.getFileSourceDir() + fileName);
        // 处理完毕
        List<String> newParagraphList = new LinkedList<>();
        if (!CollectionUtils.isEmpty(paragraphList)) {
            // 重复率
            AtomicInteger dup = new AtomicInteger();
            paragraphList.forEach(item -> {
                List<SearchVo> searchVoList = luceneTool.search(item);
                if (!CollectionUtils.isEmpty(searchVoList)) {
                    // 此处加红
                    item = "<font color=red>" + item + "</font>";
                    dup.getAndIncrement();
                }
                newParagraphList.add(item);
            });
            // 保存重复率
            DecimalFormat df = new DecimalFormat("0.00%");
            BigDecimal d1 = new BigDecimal(dup.intValue());
            BigDecimal d2 = new BigDecimal(paragraphList.size());
            String percent = df.format(d1.divide(d2, 2, BigDecimal.ROUND_HALF_UP));
            logger.info("repeat percent:{}", percent);
            List<FileEntity> fileEntityList = fileDao.findAllByFileName(fileName);
            if (!CollectionUtils.isEmpty(fileEntityList)) {
                FileEntity fileEntity = fileEntityList.get(0);
                fileEntity.setPercent(percent);
                fileDao.save(fileEntity);
            }
        }
        WordUtils.newWord(newParagraphList, configProperties.getFileResultDir() + ConstantSystem.VERSION + fileName);
    }
}
