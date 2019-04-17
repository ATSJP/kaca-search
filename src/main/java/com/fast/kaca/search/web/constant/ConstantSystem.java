package com.fast.kaca.search.web.constant;

/**
 * @author sys
 * @date 2019/4/17
 **/
public interface ConstantSystem {

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
