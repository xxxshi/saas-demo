package com.shicc.customercenter.security.server.oauth2;

import com.taoqicar.uaaaccount.config.MetricsConfiguration;
import com.taoqicar.uaaaccount.service.dto.MobileAuthCredentials;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * @description:
 * @author: ChangFeng
 * @create: 2018-12-12 14:07
 **/
public class MobileAuthenticationProvider implements AuthenticationProvider {

    private final Logger log = LoggerFactory.getLogger(MetricsConfiguration.class);

    private UserDetailsService userDetailsService;

    private MobileVerifyCodeService mobileVerifyCodeService;

    private PasswordEncoder passwordEncoder;

    /**
     * APP上线账号
     */
    private static final String PHONE_NUM = "15288889999";
    private static final String DEFAULT_CODE = "999999";

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public void setMobileVerifyCodeService(MobileVerifyCodeService mobileVerifyCodeService) {
        this.mobileVerifyCodeService = mobileVerifyCodeService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(MobileAuthentication.class, authentication);

        // Determine phone
        String phone = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

        UserDetails user;

        // pre check
        this.preAuthenticationCheck(authentication);
        try {
            user = retrieveUser(phone,
                (MobileAuthentication) authentication);
        } catch (UsernameNotFoundException notFound) {
            log.error("Phone '" + phone + "' not found");
            throw notFound;
        }
        // post check
        this.postAuthenticationCheck(user);

        return createSuccessAuthentication(user, authentication, user);
    }

    protected void preAuthenticationCheck(Authentication authentication) {
        if (authentication.getCredentials() == null) {
            log.error("Authentication failed: no verify code provided");

            throw new BadCredentialsException("Bad credentials");
        }
        MobileAuthCredentials mobileAuthCredentials = (MobileAuthCredentials) authentication.getCredentials();
        // check verifyCode
        String encodedVerifyCode = mobileVerifyCodeService.loadVerifyCodeByPhoneAndBusiness(
            authentication.getPrincipal().toString(),
            mobileAuthCredentials.getEndpoint()
        );
        if (ObjectUtils.equals(authentication.getPrincipal().toString(), PHONE_NUM)) {
            if (ObjectUtils.equals(DEFAULT_CODE, mobileAuthCredentials.getVerifyCode())) {
                log.info("审核账号:{},{}", mobileAuthCredentials.getVerifyCode(), authentication.getPrincipal());
                return;
            }
        }
        if (!StringUtils.equals(mobileAuthCredentials.getVerifyCode(), encodedVerifyCode)) {
            throw new BadCredentialsException("Bad credentials");
        }
        mobileVerifyCodeService.removeLoadVerifyCodeByPhoneAndBusiness(
            authentication.getPrincipal().toString(),
            mobileAuthCredentials.getEndpoint()
        );

    }

    protected void postAuthenticationCheck(UserDetails userDetails) {

    }

    protected Authentication createSuccessAuthentication(Object principal,
                                                         Authentication authentication, UserDetails user) {
        MobileAuthentication result = new MobileAuthentication(
            principal, authentication.getCredentials(), user.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    protected UserDetails retrieveUser(String username, MobileAuthentication authentication) throws AuthenticationException {
        try {
            UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
            if (loadedUser == null) {
                throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
            }
            return loadedUser;
        } catch (UsernameNotFoundException ex) {
            throw ex;
        } catch (InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (MobileAuthentication.class
            .isAssignableFrom(authentication));
    }
}
