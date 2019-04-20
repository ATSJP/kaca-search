package com.fast.kaca.search.web.utils;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * word工具
 *
 * @author sys
 * @date 2019/4/14
 **/
public class WordUtils {
    private static final Logger logger = LoggerFactory.getLogger(WordUtils.class);
    /**
     * HWPF是操作Microsoft Word 97（-2007）文件的标准API入口。它还支持对旧版Word 6和Word 95文件对有限的只读功能。
     * XWPF是操作Microsoft Word 2007文件的标准API入口。
     */
    private static final String WORD_2007 = ".doc";
    private static final String WORD_2010 = ".docx";

    /**
     * 读取word文档
     *
     * @param filePath 文件路径
     * @return 段落切割的list（不含图片）
     */
    public static List<String> readWordFile(String filePath) {
        List<String> contextList = Lists.newArrayList();
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(filePath));
            // 默认都按照段落来读取
            if (filePath.endsWith(WORD_2007)) {
                getParagraphsFromWord2003(contextList, stream);
            } else if (filePath.endsWith(WORD_2010)) {
                getParagraphsFromWord2007(contextList, stream);
            }
        } catch (IOException e) {
            logger.debug("读取word文件失败->e:{}", e.getMessage());
        } finally {
            closeStream(stream);
        }
        return contextList;
    }

    /**
     * 从word2003即老版本，以段落为切割
     *
     * @param contextList 内容list
     * @param stream      流
     * @throws IOException e
     */
    private static void getParagraphsFromWord2003(List<String> contextList, InputStream stream) throws IOException {
        HWPFDocument document = new HWPFDocument(stream);
        WordExtractor extractor = new WordExtractor(document);
        String[] contextArray = extractor.getParagraphText();
        Arrays.asList(contextArray).forEach(context -> contextList.add(CharMatcher.whitespace().removeFrom(context)));
        extractor.close();
        document.close();
    }

    /**
     * 从word2007即新版本，以段落为切割
     *
     * @param contextList 内容list
     * @param stream      流
     * @throws IOException e
     */
    private static void getParagraphsFromWord2007(List<String> contextList, InputStream stream) throws IOException {
        XWPFDocument document = new XWPFDocument(stream).getXWPFDocument();
        List<XWPFParagraph> paragraphList = document.getParagraphs();
        paragraphList.forEach(paragraph -> contextList.add(CharMatcher.whitespace().removeFrom(paragraph.getParagraphText())));
        document.close();
    }

    /**
     * 关闭流
     *
     * @param stream 流
     */
    private static void closeStream(InputStream stream) {
        if (null != stream) {
            try {
                stream.close();
            } catch (IOException e) {
                logger.debug("关闭word文件失败->e:{}", e.getMessage());
            }
        }
    }

    public static void newWord(List<String> newParagraphList, String filePath)  {
        XWPFDocument document = new XWPFDocument();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 添加标题
        XWPFParagraph titleParagraph = document.createParagraph();
        // 设置段落居中
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText("论文检测结果");
        titleParagraphRun.setColor("000000");
        titleParagraphRun.setFontSize(20);

        newParagraphList.forEach(item->{
            String fontColor = "000000";
            String bgColor = "";
            if (item.startsWith("<font color=red>") || item.endsWith("</font>")) {
                item = item.replaceAll("<font color=red>", "").replaceAll("</font>", "");
                fontColor = "696969";
                bgColor = "97FFFF";
            }
            // 段落
            XWPFParagraph firstParagraph = document.createParagraph();
            XWPFRun run = firstParagraph.createRun();
            run.setText(item);
            run.setColor(fontColor);
            run.setFontSize(16);

            //设置段落背景颜色
            CTShd cTShd = run.getCTR().addNewRPr().addNewShd();
            cTShd.setVal(STShd.CLEAR);
            cTShd.setFill(bgColor);

            // 换行
            XWPFParagraph paragraph1 = document.createParagraph();
            XWPFRun paragraphRun1 = paragraph1.createRun();
            paragraphRun1.setText("\r");
        });

//        // 基本信息表格
//        XWPFTable infoTable = document.createTable();
//        // 去表格边框
//        infoTable.getCTTbl().getTblPr().unsetTblBorders();
//
//        // 列宽自动分割
//        CTTblWidth infoTableWidth = infoTable.getCTTbl().addNewTblPr().addNewTblW();
//        infoTableWidth.setType(STTblWidth.DXA);
//        infoTableWidth.setW(BigInteger.valueOf(9072));
//
//        // 表格第一行
//        XWPFTableRow infoTableRowOne = infoTable.getRow(0);
//        infoTableRowOne.getCell(0).setText("职位");
//        infoTableRowOne.addNewTableCell().setText(": Java 开发工程师");
//
//        // 表格第二行
//        XWPFTableRow infoTableRowTwo = infoTable.createRow();
//        infoTableRowTwo.getCell(0).setText("姓名");
//        infoTableRowTwo.getCell(1).setText(": seawater");
//
//        // 表格第三行
//        XWPFTableRow infoTableRowThree = infoTable.createRow();
//        infoTableRowThree.getCell(0).setText("生日");
//        infoTableRowThree.getCell(1).setText(": xxx-xx-xx");
//
//        // 表格第四行
//        XWPFTableRow infoTableRowFour = infoTable.createRow();
//        infoTableRowFour.getCell(0).setText("性别");
//        infoTableRowFour.getCell(1).setText(": 男");
//
//        // 表格第五行
//        XWPFTableRow infoTableRowFive = infoTable.createRow();
//        infoTableRowFive.getCell(0).setText("现居地");
//        infoTableRowFive.getCell(1).setText(": xx");
//        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
//        XWPFHeaderFooterPolicy policy = new XWPFHeaderFooterPolicy(document, sectPr);

        // 添加页眉
        CTP ctpHeader = CTP.Factory.newInstance();
        CTR ctrHeader = ctpHeader.addNewR();
        CTText ctHeader = ctrHeader.addNewT();
        String headerText = "论文检测";
        ctHeader.setStringValue(headerText);
        XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeader, document);
        // 设置为右对齐
        headerParagraph.setAlignment(ParagraphAlignment.RIGHT);
//        XWPFParagraph[] parsHeader = new XWPFParagraph[1];
//        parsHeader[0] = headerParagraph;
//        policy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, parsHeader);

        // 添加页脚
        CTP ctpFooter = CTP.Factory.newInstance();
        CTR ctrFooter = ctpFooter.addNewR();
        CTText ctFooter = ctrFooter.addNewT();
        String footerText = "论文检测";
        ctFooter.setStringValue(footerText);
//        XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooter, document);
        headerParagraph.setAlignment(ParagraphAlignment.CENTER);
//        XWPFParagraph[] parsFooter = new XWPFParagraph[1];
//        parsFooter[0] = footerParagraph;
//        policy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, parsFooter);

        try {
            document.write(out);
            if (out != null) {
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
