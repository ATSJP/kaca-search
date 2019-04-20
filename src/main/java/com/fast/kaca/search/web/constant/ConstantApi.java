package com.fast.kaca.search.web.constant;

/**
 * @author sys
 * @date 2019/4/15
 **/
public interface ConstantApi {

    enum CODE {
        /**
         * 系统错误code
         */
        SUCCESS(0, "成功"),
        FAIL(1, "失败"),
        SYSTEM_ERROR(2, "系统错误"),
        ILLEGAL_REQUEST(3, "非法请求"),
        TOKEN_INVALID(4, "token失效,请重新登陆"),
        SELF_1(5, "自定义错误1"),
        SELF_2(6, "自定义错误2");

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

    enum LOGIN_MESSAGE {
        /**
         * 返回状态及自定义信息
         */
        SUCCESS("登陆成功"),
        FAIL("登陆失败，请确认您的用户名或者密码");

        private String desc;

        LOGIN_MESSAGE(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum REGISTER_MESSAGE {
        /**
         * 返回状态及自定义信息
         */
        SUCCESS("注册成功"),
        FAIL("注册失败，用户名已存在");

        private String desc;

        REGISTER_MESSAGE(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum FILE_UPLOAD {
        /**
         * 文件上传自定义提示
         */
        SUCCESS("上传成功，正在查重"),
        FAIL("上传失败，文件不为空");

        private String desc;

        FILE_UPLOAD(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum FILE_LIST {
        /**
         * 文件list
         */
        SUCCESS("没有找到任何文章哦");

        private String desc;

        FILE_LIST(String desc) {
            this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }
    }

    enum IS_TRUE {
        /**
         * 0 否 1 是
         */
        FALSE((short) 0, "否"),
        TRUE((short) 1, "是");

        private Short code;
        private String desc;

        IS_TRUE(Short code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Short getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
