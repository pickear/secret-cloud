package com.weasel.secret.cloud.infrastructure.shiro;

import com.google.common.collect.Maps;
import com.weasel.secret.cloud.infrastructure.shiro.filter.AutoLoginFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by dell on 2017/2/23.
 */
@Configuration
public class ShiroConfiguration {

	public static final int THREE_MONTH = 60 * 60 * 24 * 30 * 3;
	public static final int ONE_SECOND = 1000*1;
	public static final int THIRTY_SECOND = ONE_SECOND * 30;
	public static final int ONE_MINUTE = ONE_SECOND * 60;
	public static final int THIRTY_MINUTE = ONE_MINUTE * 30;
	public static final int ONE_HOUR = ONE_MINUTE * 60;

	private String cipherKey = "SrpFBcVD89eTQ2icOD0TMg==";


	@Bean(name = "shiroFilter")
	public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

		ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
		shiroFilterFactoryBean.setSecurityManager(securityManager());
		Map<String, Filter> filters = Maps.newHashMap();
		filters.put("login",new AutoLoginFilter());
		shiroFilterFactoryBean.setFilters(filters);
		shiroFilterFactoryBean.setLoginUrl("/login");
		shiroFilterFactoryBean.setSuccessUrl("/");
		shiroFilterFactoryBean.setUnauthorizedUrl("/403");
		Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
		filterChainDefinitionMap.put("/subject/**", "login,authc");
		filterChainDefinitionMap.put("/user/query", "login,authc");
		filterChainDefinitionMap.put("/user/logined", "login,anon");
		filterChainDefinitionMap.put("/**", "anon");
		shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

		return shiroFilterFactoryBean;
	}

	@Bean
	public SecurityManager securityManager() {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(userRealm());
		securityManager.setSessionManager(sessionManager());

		CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
		rememberMeManager.getCookie().setMaxAge(THREE_MONTH);
		rememberMeManager.setCipherKey(Base64.decode(cipherKey.getBytes()));
		securityManager.setRememberMeManager(rememberMeManager);
		return securityManager;
	}

	@Bean
	public SessionManager sessionManager() {
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
		sessionManager.setSessionValidationInterval(THIRTY_MINUTE); // 半小时检查一次session有效性
		sessionManager.setGlobalSessionTimeout(ONE_HOUR); // session 1小时过期
		return sessionManager;
	}

	@Bean
	public ShiroRealm userRealm() {
		ShiroRealm userRealm = new ShiroRealm();
		return userRealm;
	}

	@Bean(name = "credentialsMatcher")
	public HashedCredentialsMatcher hashedCredentialsMatcher() {
		HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

		hashedCredentialsMatcher.setHashAlgorithmName("md5");// 散列算法:这里使用MD5算法;
		hashedCredentialsMatcher.setHashIterations(2);// 散列的次数，比如散列两次，相当于 md5(md5(""));
		// storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
		hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);

		return hashedCredentialsMatcher;
	}

	@Bean
	public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
		return new LifecycleBeanPostProcessor();
	}

	@Bean
	@DependsOn({ "lifecycleBeanPostProcessor" })
	public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
		DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
		advisorAutoProxyCreator.setProxyTargetClass(true);
		return advisorAutoProxyCreator;
	}

	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
		AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
		authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
		return authorizationAttributeSourceAdvisor;
	}

}
