package com.fast.kaca.search.web.constant;

/**
 * @author sys
 * @date 2019/4/15
 **/
public interface ConstantApi {

    enum CODE {
        /**
         * 返回状态
         */
        SUCCESS(0, "success"),
        ERROR(1, "error");

        private int code;
        private String desc;

        CODE(int code, String desc) {
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

    enum TOKEN_STATUS {
        /**
         * 返回token状态
         */
        NOT_EFFECT(1, "token失效,请重新登陆");

        private int code;
        private String desc;

        TOKEN_STATUS(int code, String desc) {
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

    enum LOGIN_MESSAGE {
        /**
         * 返回状态及自定义信息
         */
        SUCCESS(0, "登陆成功"),
        ERROR(1, "登陆失败，请确认您的用户名或者密码");

        private int code;
        private String desc;

        LOGIN_MESSAGE(int code, String desc) {
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
