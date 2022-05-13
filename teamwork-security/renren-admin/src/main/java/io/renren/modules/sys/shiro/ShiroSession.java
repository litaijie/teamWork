package io.renren.modules.sys.shiro;


import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 目的: shiro 的 session 管理
 *      自定义session规则，实现前后分离，在跨域等情况下使用token 方式进行登录验证才需要，否则没必须使用本类。
 *      shiro默认使用 ServletContainerSessionManager 来做 session 管理，它是依赖于浏览器的 cookie 来维护 session 的,
 *      调用 storeSessionId  方法保存sesionId 到 cookie中
 *      为了支持无状态会话，我们就需要继承 DefaultWebSessionManager
 *      自定义生成sessionId 则要实现 SessionIdGenerator
 *
 * @author 小鸟的胖次
 */
@Component
public class ShiroSession extends DefaultWebSessionManager {
    /**
     * 定义的请求头中使用的标记key，用来传递 token
     */
    private static final String AUTH_TOKEN = "authToken";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";
    @Value("${renren.globalSessionTimeout:3600}")
    private long globalSessionTimeout;

    public ShiroSession() {
        super();
        //设置 shiro session 失效时间，默认为30分钟，这里现在设置为15分钟
//        setGlobalSessionTimeout(globalSessionTimeout * 1000);
    }



    /**
     * 获取sessionId，原本是根据sessionKey来获取一个sessionId
     * 重写的部分多了一个把获取到的token设置到request的部分。这是因为app调用登陆接口的时候，是没有token的，登陆成功后，产生了token,我们把它放到request中，返回结
     * 果给客户端的时候，把它从request中取出来，并且传递给客户端，客户端每次带着这个token过来，就相当于是浏览器的cookie的作用，也就能维护会话了
     * @param request ServletRequest
     * @param response ServletResponse
     * @return Serializable
     */
    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        //获取请求头中的 AUTH_TOKEN 的值，如果请求头中有 AUTH_TOKEN 则其值为sessionId。shiro就是通过sessionId 来控制的
        String sessionId = WebUtils.toHttp(request).getHeader(AUTH_TOKEN);
        if (StringUtils.isEmpty(sessionId)){
            //如果没有携带id参数则按照父类的方式在cookie进行获取sessionId
            return super.getSessionId(request, response);

        } else {
            //请求头中如果有 authToken, 则其值为sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            //sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, sessionId);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return sessionId;
        }
    }

//    private static final Logger log = LoggerFactory.getLogger(DefaultWebSessionManager.class);


//    private String authorization = "authToken";
//
//    /**
//     * 重写获取sessionId的方法调用当前Manager的获取方法
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    @Override
//    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
//        return this.getReferencedSessionId(request, response);
//    }
//
//    /**
//     * 获取sessionId从请求中
//     *
//     * @param request
//     * @param response
//     * @return
//     */
//    private Serializable getReferencedSessionId(ServletRequest request, ServletResponse response) {
//        String id = this.getSessionIdCookieValue(request, response);
//        if (id != null) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "cookie");
//        } else {
//            id = this.getUriPathSegmentParamValue(request, "JSESSIONID");
//            if (id == null) {
//                // 获取请求头中的session
//                id = WebUtils.toHttp(request).getHeader(this.authorization);
//                if (id == null) {
//                    String name = this.getSessionIdName();
//                    id = request.getParameter(name);
//                    if (id == null) {
//                        id = request.getParameter(name.toLowerCase());
//                    }
//                }
//            }
//            if (id != null) {
//                request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, "url");
//            }
//        }
//
//        if (id != null) {
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
//            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
//        }
//        //log.info("id: "+id);
//        return id;
//    }
//
//    // copy super
//    private String getSessionIdCookieValue(ServletRequest request, ServletResponse response) {
//        if (!this.isSessionIdCookieEnabled()) {
////            log.debug("Session ID cookie is disabled - session id will not be acquired from a request cookie.");
//            return null;
//        } else if (!(request instanceof HttpServletRequest)) {
////            log.debug("Current request is not an HttpServletRequest - cannot get session ID cookie.  Returning null.");
//            return null;
//        } else {
//            HttpServletRequest httpRequest = (HttpServletRequest) request;
//            return this.getSessionIdCookie().readValue(httpRequest, WebUtils.toHttp(response));
//        }
//    }
//
//    // copy super
//    private String getUriPathSegmentParamValue(ServletRequest servletRequest, String paramName) {
//        if (!(servletRequest instanceof HttpServletRequest)) {
//            return null;
//        } else {
//            HttpServletRequest request = (HttpServletRequest) servletRequest;
//            String uri = request.getRequestURI();
//            if (uri == null) {
//                return null;
//            } else {
//                int queryStartIndex = uri.indexOf(63);
//                if (queryStartIndex >= 0) {
//                    uri = uri.substring(0, queryStartIndex);
//                }
//
//                int index = uri.indexOf(59);
//                if (index < 0) {
//                    return null;
//                } else {
//                    String TOKEN = paramName + "=";
//                    uri = uri.substring(index + 1);
//                    index = uri.lastIndexOf(TOKEN);
//                    if (index < 0) {
//                        return null;
//                    } else {
//                        uri = uri.substring(index + TOKEN.length());
//                        index = uri.indexOf(59);
//                        if (index >= 0) {
//                            uri = uri.substring(0, index);
//                        }
//
//                        return uri;
//                    }
//                }
//            }
//        }
//    }
//
//    // copy super
//    private String getSessionIdName() {
//        String name = this.getSessionIdCookie() != null ? this.getSessionIdCookie().getName() : null;
//        if (name == null) {
//            name = "JSESSIONID";
//        }
//
//        return name;
//    }
}