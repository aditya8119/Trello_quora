package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignoutService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthorizationService authorizationService;

    @Transactional
    public UserAuthTokenEntity getUser(final String authorizationToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        return userAuthEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String signOut(final String authorization)
            throws SignOutRestrictedException {
         UserAuthTokenEntity userAuthTokenEntity = authorizationService.fetchAuthTokenEntity(authorization);
        if (userAuthTokenEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        else if (userAuthTokenEntity.getLogoutAt()!= null){
            throw new SignOutRestrictedException("SGR-002" , "User is already SignOut");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        userAuthTokenEntity.setLogoutAt(now);
        userDao.setUserLogout(userAuthTokenEntity);


        return userDao.signOut(authorization);
    }
}