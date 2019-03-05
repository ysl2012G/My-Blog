package com.my.blog.website.config;


import com.my.blog.website.utils.TaleUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

/**
 * 向mvc中添加自定义组件 Created by BlueT on 2017/3/9.
 */
@Component
public class WebMvcConfig implements WebMvcConfigurer {
//    @Resource
//    private BaseInterceptor baseInterceptor;
//
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
////        registry.addInterceptor(baseInterceptor);
//    }

    /**
     * 添加静态资源文件，外部可以直接访问地址
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**").addResourceLocations("file:" + TaleUtils.getUploadFilePath() + "upload/");
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/templates/**").addResourceLocations("classpath:/templates/");
        //        super.addResourceHandlers(registry);
        //        addResourceHandlers(registry);
    }

    //检测是否为静态资源
    @Bean
    public ResourceUrlProvider resourceUrlProvider() {
        return new ResourceUrlProvider();
    }


}

//}}
