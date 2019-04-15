package com.fast.kaca.search;

import com.fast.kaca.search.core.service.SearchService;
import com.fast.kaca.search.core.utils.WordUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KacaSearchApplicationTests {

    @Resource
    SearchService searchService;

    @Test
    public void contextLoads() {
    }

    /**
     * 单元测试->读取word文档
     */
    @Test
    public void readWordFile() {
        List<String> contextList = WordUtils.readWordFile("C:\\Users\\sjp\\Desktop\\source\\bbs论文.docx");
        if (!CollectionUtils.isEmpty(contextList)) {
            contextList.forEach(System.out::println);
        }
    }

    @Test
    public void createIndex() {
        long start = System.currentTimeMillis();
        searchService.createIndex();
        long end = System.currentTimeMillis();
        System.out.println("time:" + (end - start));
    }

}
