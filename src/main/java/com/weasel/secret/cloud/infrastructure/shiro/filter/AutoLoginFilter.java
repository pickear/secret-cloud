package com.weasel.secret.cloud.infrastructure.shiro.filter;

import com.weasel.secret.common.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.servlet.AdviceFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Created by dell on 2017/12/4.
 */
public class AutoLoginFilter extends AdviceFilter {

    private final static Logger logger = LoggerFactory.getLogger(AutoLoginFilter.class);

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isRemembered()) {
            User user = (User) SecurityUtils.getSubject().getPrincipal();
            if(null != user){
                logger.info("用户[{}]自动登录!",user.getUsername());
               /* user = userService.findByUsername(user.getUsername());*/
                try {
                    UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword(), true);
                    currentUser.login(token); // 登录
                } catch (IncorrectCredentialsException exception) {
                    logger.warn("用户[{}]输入的密码不正确。", user.getUsername());
                } catch (UnknownAccountException exception) {
                    logger.warn("用户名[{}]不存在。", user.getUsername());
                }
            }
        }
        return true;
    }

}
