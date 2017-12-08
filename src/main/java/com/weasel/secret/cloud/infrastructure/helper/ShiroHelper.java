package com.weasel.secret.cloud.infrastructure.helper;

import com.weasel.secret.common.domain.User;
import org.apache.shiro.SecurityUtils;

/**
 * Created by dell on 2017/11/14.
 */
public final class ShiroHelper {

    /**
     * 获取当前用户
     * @return
     */
    public static User getCurrentUser(){
        org.apache.shiro.subject.Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated() || currentUser.isRemembered()){
            return (User) currentUser.getPrincipal();
        }
        return null;
    }
    protected ShiroHelper(){}
}
