package com.fast.kaca.search.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fast.kaca.search.web.constant.ConstantApi;
import com.fast.kaca.search.web.response.BaseResponse;
import com.fast.kaca.search.web.utils.RedissonTools;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限认证
 *
 * @author sys
 * @date 2019/4/16
 **/
@Component
public class ApiAuthInterceptor implements HandlerInterceptor {

    @Resource
    private RedissonTools redissonTools;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        // TODO 此方式 黑客可以通过修改uid，而绕过校验，应提供时间等参数，从前端带入，后台md5 对比 token，token随时间偏移
        String uid = request.getParameter("uid");
        String token = request.getParameter("token");
        if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(token)) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(ConstantApi.CODE.ILLEGAL_REQUEST.getCode());
            baseResponse.setMsg(ConstantApi.CODE.ILLEGAL_REQUEST.getDesc());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(baseResponse));
            return false;
        }
        String token1 = redissonTools.get("token-" + uid);
        if (!token.equals(token1)) {
            BaseResponse baseResponse = new BaseResponse();
            baseResponse.setCode(ConstantApi.CODE.TOKEN_INVALID.getCode());
            baseResponse.setMsg(ConstantApi.CODE.TOKEN_INVALID.getDesc());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(baseResponse));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

}
