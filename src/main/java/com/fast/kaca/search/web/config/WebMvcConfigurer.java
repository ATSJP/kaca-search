package com.fast.kaca.search.web.config;

import com.fast.kaca.search.web.interceptor.ApiAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * @author sjp
 * @date 2019/4/16
 **/
@Configuration
public class WebMvcConfigurer extends WebMvcConfigurationSupport {

    @Resource
    private ApiAuthInterceptor apiAuthInterceptor;

///    /**
//     * 功能描述:
//     * 配置静态资源,避免静态资源请求被拦截
//     */
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/");
//        registry.addResourceHandler("/templates/**")
//                .addResourceLocations("classpath:/templates/");
//        super.addResourceHandlers(registry);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/")
                .excludePathPatterns("/user/login");
        super.addInterceptors(registry);
    }

}
