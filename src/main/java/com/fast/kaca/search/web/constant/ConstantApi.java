package com.fast.kaca.search.web.constant;

/**
 * @author sys
 * @date 2019/4/15
 **/
public interface ConstantApi {

    public enum code {
        /**
         * 返回状态
         */
        SUCCESS(0, "success"),
        ERROR(1, "error");

        private int code;
        private String desc;

        code(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

}
