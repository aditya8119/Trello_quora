package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import com.upgrad.quora.service.type.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;


    //CreateQuestion Service
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorization) throws AuthorizationFailedException {
        //String [] bearerToken = authorization.split("Bearer ");
        // System.out.println("Bearer Token "+bearerToken[1]);
        System.out.println("Authorisation "+authorization);
        UserAuthTokenEntity userAuthToken=userDao.getUserAuthToken(authorization);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }
        if(userAuthToken.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to post a question");
        }
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthToken.getUser());
        questionEntity.setDate(ZonedDateTime.now());
        return questionDao.createQuestion(questionEntity);
    }

    //Get Question By UUID
    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getQuestionByUserId(final String uuid, final String authorization) throws AuthorizationFailedException,UserNotFoundException {
        //String [] bearerToken = authorization.split("Bearer ");
        // System.out.println("Bearer Token "+bearerToken[1]);
        System.out.println("Authorisation "+authorization);
        UserAuthTokenEntity userAuthToken=userDao.getUserAuthToken(authorization);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }
        if(userAuthToken.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to get all questions posted by a specific user");
        }
        List<QuestionEntity> questionEntityList=questionDao.getQuestionByUserId(uuid);
        if (questionEntityList.isEmpty() || questionEntityList.size()<=0) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionEntityList;
    }

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
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }

        questionDao.deleteQuestion(questionId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(final String authorization) throws AuthorizationFailedException{
        System.out.println("QuestionService.getAllQuestions: authorization :"+ authorization);
        UserAuthTokenEntity userAuthToken=userDao.getUserAuthToken(authorization);
        if (userAuthToken == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if(userAuthToken.getLogoutAt()!=null)
        {
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        List<QuestionEntity> questionEntityList=questionDao.getAllQuestions();

        return questionEntityList;
    }

    // An abstract interface checks whether the Question is asked by the owner

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity isUserQuestionOwner(String questionUuId, UserAuthTokenEntity authorizedUser, ActionType actionType) throws AuthorizationFailedException, InvalidQuestionException {
        QuestionEntity question = questionDao.getQuestionById(questionUuId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        } else if (!question.getUser().getUuid().equals(authorizedUser.getUser().getUuid())) {
            if (actionType.equals(ActionType.DELETE_QUESTION)) {
                if (authorizedUser.getUser().getRole().equals(RoleType.admin.toString())) {
                    return question;
                } else {
                    throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
                }

            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        } else {
            return question;
        }
    }


    //An abstract interface to edit the Question

    @Transactional(propagation = Propagation.REQUIRED)
    public void editQuestion(QuestionEntity question) {
        questionDao.editQuestion(question);
    }

}
