package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
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
   * @param answerEntity  entity for creating the answer
   * @param questionId    The questionid to which the answer belongs to
   * @param authorization access token of the user
   * @return AnswerEntity The created answer entity
   * @throws AuthorizationFailedException ATHR-001 - User has not signed in, ATHR-002-User is signed
   *                                      out.Sign in first to post an answer
   * @throws InvalidQuestionException     QUES-001 - The question entered is invalid
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
   * @param answerId      The id of the answer to be deleted
   * @param authorization The access token of the user
   * @throws AnswerNotFoundException      ANS-001 - Entered answer uuid does not exist
   * @throws AuthorizationFailedException ATHR-001 - User has not signed in, ATHR-002 - User is
   *                                      signed out.Sign in first to delete an answer, ATHR-003-
   *                                      Only the answer owner or admin can delete the answer
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteAnswer(final String answerId, final String authorization)
      throws AnswerNotFoundException, AuthorizationFailedException {
    UserAuthTokenEntity userAuthEntity = userDao.getUserAuthToken(authorization);

    // Validate if user is signed in or not
    if (userAuthEntity == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }

    // Validate if user has signed out
    if (userAuthEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to delete an answer");
    }

    // Validate if requested answer exists or not
    if (answerDao.getAnswerByUUId(answerId) == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }

    // Validate if current user is the owner of requested answer or the role of user is not nonadmin
    if (!userAuthEntity.getUser().getUuid()
        .equals(answerDao.getAnswerByUUId(answerId).getUser().getUuid())) {
      if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
        throw new AuthorizationFailedException("ATHR-003",
            "Only the answer owner or admin can delete the answer");
      }
    }

    answerDao.deleteAnswer(answerId);
  }

  /**
   * Service implementation for getAllAnswersToQuestion API
   *
   * @param questionId    question id whose answers are to be retrieved from the database
   * @param authorization access token of the signed in user
   * @return The answer entity object for the requested question id
   * @throws AuthorizationFailedException ATHR-001 - User has not signed in , ATHR-002 - User is
   *                                      signed out.Sign in first to get the answers
   * @throws InvalidQuestionException     QUES-001 - The question with entered uuid whose details
   *                                      are to be seen does not exist"
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public List<AnswerEntity> getAllAnswersToQuestion(final String questionId,
      final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
    UserAuthTokenEntity userAuthToken = userDao.getUserAuthToken(authorization);
    // Validate if user is signed in or not
    if (userAuthToken == null) {
      throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
    }
    // Validate if user has signed out
    if (userAuthToken.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to get the answers");

    }
    // Validate if requested question exists or not
    if (questionDao.getQuestionByQUuid(questionId) == null) {
      throw new InvalidQuestionException("QUES-001",
          "The question with entered uuid whose details are to be seen does not exist");
    }

    List<AnswerEntity> answerEntityList = answerDao.getAllAnswersToQuestion(questionId);
    return answerEntityList;

  }


  /**
   * Method to get answer with the uuid
   * @param uuid id of the answer
   * @return Answer entity object for the requested answer
   * @throws AnswerNotFoundException ANS-001-Entered answer uuid does not exist
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity getAnswerFromId(String uuid) throws AnswerNotFoundException {
    AnswerEntity answerEntity = answerDao.getAnswerByUUId(uuid);
    if (answerEntity == null) {
      throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
    }
    return answerEntity;
  }

  /**
   * Method to check if answer belong to user
   * @param userAuthTokenEntity singned in user authentication details
   * @param answerEntity the answer entity object
   * @return The answer entity object
   * @throws AuthorizationFailedException ATHR-002-
   *           User is signed out.Sign in first to edit an answer, ATHR-003-
   *           Only the answer owner can edit or delete the answer
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity checkAnswerBelongToUser(UserAuthTokenEntity userAuthTokenEntity,
      AnswerEntity answerEntity) throws AuthorizationFailedException {

    UserEntity userEntity = userAuthTokenEntity.getUser();

    if (userAuthTokenEntity.getLogoutAt() != null) {
      throw new AuthorizationFailedException("ATHR-002",
          "User is signed out.Sign in first to edit an answer");
    }
    String auuid = answerEntity.getUuid();
    String uuuid = userEntity.getUuid();
    AnswerEntity checkedAnswer = answerDao.checkAnswerBelongToUser(auuid, uuuid);
    if (checkedAnswer == null) {
      throw new AuthorizationFailedException("ATHR-003",
          "Only the answer owner can edit or delete the answer");
    }
    return checkedAnswer;
  }

  /**
   * Method to update answer
   * @param answerEntity The entity object of the answer
   * @return returns the updated answer entity object
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public AnswerEntity updateAnswer(AnswerEntity answerEntity) {
    return answerDao.updateAnswer(answerEntity);
  }


}
