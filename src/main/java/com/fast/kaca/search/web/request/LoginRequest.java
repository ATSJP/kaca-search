package com.fast.kaca.search.web.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author sys
 * @date 2019/4/16
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginRequest extends BaseRequest {
    /**
     * 用户名
     */
    @NotBlank
    @Length(min = 1, max = 50)
    private String userName;
    /**
     * 密码
     */
    @NotBlank
    @Length(min = 1, max = 100)
    private String password;
}
