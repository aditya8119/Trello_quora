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


    /**
     * Method to check if user has signed in
     * @param authorization access token of the user
     * @return true if signed in false if not signed in
     */
    public boolean hasUserSignedIn(final String authorization) {
        return userDao.hasUserSignedIn(authorization);
    }


    /**
     * Method to check is the token is valid
     * @param authorization
     * @param actionType
     * @return authorized user entity
     * @throws AuthorizationFailedException
     */
    public UserAuthTokenEntity isValidActiveAuthToken(final String authorization, Enum<ActionType> actionType) throws AuthorizationFailedException {
        return userDao.isValidActiveAuthToken(authorization, actionType);
    }


    /**
     * Method to fetch the token entity
     * @param authorization access token of the user
     * @return Auhorized user entity
     * @throws SignOutRestrictedException
     */
    public UserAuthTokenEntity fetchAuthTokenEntity(final String authorization) throws SignOutRestrictedException {
        final UserAuthTokenEntity fetchedUserAuthTokenEntity = userDao.getUserAuthToken(authorization);
        return fetchedUserAuthTokenEntity;
    }

}