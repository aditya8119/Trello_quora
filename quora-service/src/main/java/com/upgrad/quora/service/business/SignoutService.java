package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignoutService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorizationService authorizationService;

    @Transactional(propagation = Propagation.REQUIRED)
    public String signOut(final String authorization)
            throws SignOutRestrictedException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        return userDao.signOut(authorization);
    }
}