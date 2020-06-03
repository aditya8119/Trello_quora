package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import java.time.ZonedDateTime;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserAuthDao {

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private UserDao userDao;


  /**
   * Gets the user auth information based on the access token.
   *
   * @param accessToken access token of the user auth whose details is to be fetched.
   * @return A single user auth object or null
   */
  public UserAuthTokenEntity getUserAuthByToken(final String accessToken) {
    try {
      return entityManager
          .createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class)
          .setParameter("accessToken", accessToken)
          .getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public boolean userSignOutStatus(String authorizationToken) {
    UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
    ZonedDateTime loggedOutStatus = userAuthTokenEntity.getLogoutAt();
    ZonedDateTime loggedInStatus = userAuthTokenEntity.getLoginAt();
    if (loggedOutStatus != null && loggedOutStatus.isAfter(loggedInStatus)) {
      return true;
    } else
      return false;
  }
}





