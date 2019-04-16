package com.fast.kaca.search.web.vo;

/**
 * @author sys
 * @date 2019/4/16
 **/
public class SearchVo {
    /**
     * 文章的名称
     */
    private String articleName;
    /**
     * 文章相似内容
     */
    private String text;
    /**
     * 索引得分
     */
    private float score;

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
}
