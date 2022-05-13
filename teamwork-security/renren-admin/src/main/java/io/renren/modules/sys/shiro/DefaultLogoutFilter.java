//package io.renren.modules.sys.shiro;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import org.apache.shiro.session.SessionException;
//import org.apache.shiro.subject.Subject;
//import org.apache.shiro.web.filter.authc.LogoutFilter;
//import org.springframework.stereotype.Component;
//
//
///**
// *
// * 重写退出过滤器，防止推出登录后，一直报找不到sessionId的异常错误
// * @author chen.kai
// * @date 2020年7月22日 下午11:20:12
// *
// */
//@Component
//public class DefaultLogoutFilter extends LogoutFilter {
//
//    @Override
//    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
//        Subject subject = getSubject(request, response);
//        String redirectUrl = getRedirectUrl(request, response, subject);
//        try {
//            //清空缓存
//            subject.logout();
//        } catch (SessionException e) {
//            e.printStackTrace();
//        }
//        issueRedirect(request, response, redirectUrl);
//        return false;
//
//    }
//}