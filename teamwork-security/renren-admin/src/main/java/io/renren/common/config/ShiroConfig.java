/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.common.config;

import io.renren.modules.sys.shiro.ShiroSession;
import io.renren.modules.sys.shiro.UserRealm;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro的配置文件
 *
 * @author Mark sunlightcs@gmail.com
 */
@Configuration
public class ShiroConfig {

//    /**
//     * 单机环境，session交给shiro管理
//     */
//    @Bean
//    @ConditionalOnProperty(prefix = "renren", name = "cluster", havingValue = "false")
//    public DefaultWebSessionManager sessionManager(@Value("${renren.globalSessionTimeout:3600}") long globalSessionTimeout){
//        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
//        sessionManager.setSessionValidationSchedulerEnabled(true);
//        sessionManager.setSessionIdUrlRewritingEnabled(false);
//        sessionManager.setSessionValidationInterval(globalSessionTimeout * 1000);
//        sessionManager.setGlobalSessionTimeout(globalSessionTimeout * 1000);
//
//        return sessionManager;
//    }
//
//    /**
//     * 集群环境，session交给spring-session管理
//     */
//    @Bean
//    @ConditionalOnProperty(prefix = "renren", name = "cluster", havingValue = "true")
//    public ServletContainerSessionManager servletContainerSessionManager() {
//        return new ServletContainerSessionManager();
//    }

    @Bean("securityManager")
    public SecurityManager securityManager(UserRealm userRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setCacheManager(new EhCacheManager());
        securityManager.setRealm(userRealm);
//        securityManager.setSessionManager(sessionManager);
        securityManager.setSessionManager(sessionManager());
        securityManager.setRememberMeManager(null);

        return securityManager;
    }

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        Map<String, Filter> customFilterMap = new LinkedHashMap<>();
        customFilterMap.put("corsAuthenticationFilter", new CORSAuthenticationFilter());


        shiroFilter.setLoginUrl("/sys/login");
        shiroFilter.setUnauthorizedUrl("/sys/login");
//        shiroFilter.setUnauthorizedUrl("/");

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/swagger/**", "anon");
        filterMap.put("/v2/api-docs", "anon");
        filterMap.put("/swagger-ui.html", "anon");
        filterMap.put("/webjars/**", "anon");
        filterMap.put("/swagger-resources/**", "anon");

        filterMap.put("/statics/**", "anon");
        filterMap.put("/sys/login", "anon");
        filterMap.put("/favicon.ico", "anon");
        filterMap.put("/captcha.jpg", "anon");
        filterMap.put("/business/**", "anon");
        filterMap.put("/task/**", "anon");
        filterMap.put("/sys/file/**", "anon");
//        filterMap.put("/test/**", "authc");
        filterMap.put("/test/**", "anon");
        filterMap.put("/**", "corsAuthenticationFilter");
        shiroFilter.setFilterChainDefinitionMap(filterMap);

        //自定义过滤器
        shiroFilter.setFilters(customFilterMap);
        return shiroFilter;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean("sessionManager")
    public SessionManager sessionManager(){
        //将我们继承后重写的shiro session 注册
        ShiroSession shiroSession = new ShiroSession();
        shiroSession.setSessionIdUrlRewritingEnabled(false);
        shiroSession.setSessionValidationSchedulerEnabled(true);
        shiroSession.setSessionIdUrlRewritingEnabled(false);
        shiroSession.setSessionValidationInterval(3600 * 1000);//1小时
        shiroSession.setGlobalSessionTimeout(3600 * 1000);
//        shiroSession.setSessionIdCookie(sessionIdCookie());
        //如果后续考虑多tomcat部署应用，可以使用shiro-redis开源插件来做session 的控制，或者nginx 的负载均衡

//        shiroSession.setSessionDAO(new EnterpriseCacheSessionDAO());
        return shiroSession;
    }

//    @Bean("sessionIdCookie")
//    public SimpleCookie sessionIdCookie(){
//        SimpleCookie cookie=new SimpleCookie();
//        cookie.setName("shiro.sesssion");
//        cookie.setHttpOnly(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(3600 * 1000);
//        return cookie;
//    }
}
