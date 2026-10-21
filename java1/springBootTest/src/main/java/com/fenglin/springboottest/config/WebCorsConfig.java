package com.fenglin.springboottest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 跨域配置。
 *
 * <p>开发时前端通过 Vite 代理（/api → http://localhost:8080）访问后端，属于同源请求，
 * 其实用不到 CORS。但把这个放开后，即使直接用 {@code http://localhost:5173}
 * 或浏览器地址栏 / Postman 直连 8080 也不会被浏览器拦下，调试更省心。</p>
 *
 * <p>生产环境请把 allowedOriginPatterns 收窄成真实的域名白名单。</p>
 *
 * @author fenglin
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // allowCredentials(true) 时不能用 allowedOrigins("*")，必须用 allowedOriginPatterns
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
