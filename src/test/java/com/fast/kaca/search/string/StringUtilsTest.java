package com.fast.kaca.search.string;

import com.fast.kaca.search.web.utils.StringHelpUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author sjp
 * @date 2019/4/29
 **/
@SpringBootTest
public class StringUtilsTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void removeSuffix() {
        String fileName = "bbs论文.docx";
        System.out.println(StringHelpUtils.removeSuffix(fileName));
    }

    @Test
    public void percent() {
        DecimalFormat df = new DecimalFormat("0.00%");
        BigDecimal d1 = new BigDecimal(50);
        BigDecimal d2 = new BigDecimal(100);
        BigDecimal d3 = d1.divide(d2 ,2, BigDecimal.ROUND_HALF_UP);
        String percent = df.format(d3);
        logger.info("repeat percent:{}", percent);
    }

}
