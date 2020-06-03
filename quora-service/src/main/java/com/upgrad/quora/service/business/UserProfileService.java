package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private UserAuthDao userAuthDao;


  public UserEntity getUserByUuid(final String uuid, final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userAuthDao.getUserAuthByToken(accessToken);
    if (userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    //Check if user has signed out
    if (userAuthDao.userSignOutStatus(accessToken)) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get user details");
    }

    final UserEntity userDetails = userDao.getUserById(uuid);
    if (userDetails == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }
    return userDetails;
  }

}