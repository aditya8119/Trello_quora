package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

    @Autowired
    private UserDao userDao;

    //To check if the user with the access token is signed in / access token exists in the table
    //Returns boolean based on whether the access token is present in the table
    public boolean hasUserSignedIn(final String authorization) {
        return userDao.hasUserSignedIn(authorization);
    }


    //Written for all authorization calls for all controllers
    public UserAuthTokenEntity isValidActiveAuthToken(final String authorization, Enum<ActionType> actionType) throws AuthorizationFailedException {
        return userDao.isValidActiveAuthToken(authorization, actionType);
    }


    public UserAuthTokenEntity fetchAuthTokenEntity(final String authorization) throws SignOutRestrictedException {
        final UserAuthTokenEntity fetchedUserAuthTokenEntity = userDao.getUserAuthToken(authorization);
        return fetchedUserAuthTokenEntity;
    }

}