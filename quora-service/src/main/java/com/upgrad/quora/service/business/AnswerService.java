package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerService {

  @Autowired
  private AnswerDao answerDao;

  @Autowired
  private UserDao userDao;

  @Autowired
  private QuestionDao questionDao;


  /**
   * Service implementation for creating answer
   *
   * @param answerEntity
   * @param questionId
   * @param authorization
   * @return AnswerEntity
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String questionId,
      final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

    // Validate if user is signed in or not
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // Validate if user has signed out
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to post an answer");
    }

    // Validate if requested question exist
    QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
    if (questionEntity == null) {
      throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
    }

    answerEntity.setUuid(UUID.randomUUID().toString());
    answerEntity.setDate(ZonedDateTime.now());
    answerEntity.setUser(userAuthEntity.getUser());
    answerEntity.setQuestion(questionEntity);

    return answerDao.createAnswer(answerEntity);
  }

  /**
   * @param  answerId the first {@code String} id of the question to be deleted
   * @param  authorization the second {@code String} to check if the access is available.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteAnswer(final String answerId, final String authorization) throws AnswerNotFoundException, AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

    // Validate if user is signed in or not
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // Validate if user has signed out
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
    }

    // Validate if requested answer exists or not
    if (answerDao.getAnswerByUUId(answerId) == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }

    // Validate if current user is the owner of requested answer or the role of user is not nonadmin
    if (!userAuthEntity.getUser().getUuid().equals(answerDao.getAnswerByUUId(answerId).getUser().getUuid())) {
      if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
        throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
      }
    }

    answerDao.deleteAnswer(answerId);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public List<AnswerEntity> getAllAnswersToQuestion(final String authorization, final String questionId) throws AuthorizationFailedException, InvalidQuestionException{
    UserAuthTokenEntity userAuthToken=userDao.getUserAuthToken(authorization);
    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    if(userAuthToken.getLogoutAt()!=null)
    {
      throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");

    }
    if (questionDao.getQuestionByQUuid(questionId) == null) {
      throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
    }

    List<AnswerEntity> answerEntityList=answerDao.getAllAnswersToQuestion(questionId);
    return answerEntityList;

  }

}
