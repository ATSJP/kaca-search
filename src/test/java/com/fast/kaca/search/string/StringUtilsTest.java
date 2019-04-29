package com.fast.kaca.search.string;

import com.fast.kaca.search.web.utils.StringHelpUtils;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author sjp
 * @date 2019/4/29
 **/
@SpringBootTest
public class StringUtilsTest {

    @Test
    public void removeSuffix() {
        String fileName = "bbs论文.docx";
        System.out.println(StringHelpUtils.removeSuffix(fileName));
    }

}
