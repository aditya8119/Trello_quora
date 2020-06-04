package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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

}
