package com.fast.kaca.search.web.constant;

/**
 * @author sys
 * @date 2019/4/17
 **/
public interface ConstantSystem {
    /**
     * 1.0版本产生的对比结果
     */
    String VERSION = "VERSION_1.0_";

    enum ENV {
        /**
         * 系统环境
         */
        DEV("dev"),
        PROD("prod");

        private String desc;

        ENV(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }
}
