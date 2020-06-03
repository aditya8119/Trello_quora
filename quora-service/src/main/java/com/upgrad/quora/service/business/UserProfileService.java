package com.upgrad.quora.service.business;

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

  public UserEntity getUserByUuid(final String uuid, final String accessToken)
      throws AuthorizationFailedException, UserNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
    if(userAuthTokenEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    //Chek if user has signed out


    final UserEntity userDetails = userDao.getUserById(uuid);
    if(userDetails == null) {
      throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
    }
    return userDetails;
  }

}