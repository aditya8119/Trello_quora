package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeleteQuestionService {

  @Autowired
  private UserDao userDao;

  @Autowired
  private QuestionDao questionDao;


  /**
   * @param  questionId the first {@code String} id of the question to be deleted
   * @param  authorization the second {@code String} to check if the access is available.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteQuestion(final String questionId, final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

    // Validate if user is signed in or not
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // Validate if user has signed out
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
    }

    // Validate if requested question exist or not
    if (questionDao.getQuestionByQUuid(questionId) == null) {
      throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
    }

    // Validate if current user is the owner of requested question or the role of user is not nonadmin
    if (!userAuthEntity.getUser().getUuid().equals(questionDao.getQuestionByQUuid(questionId).getUser().getUuid())) {
      if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
        throw new AuthorizationFailedException("ATHR-003", "Oly the question owner or admin can delete the question");
      }
    }

    questionDao.deleteQuestion(questionId);
  }

}
