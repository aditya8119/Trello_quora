package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    //getAccessToken
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity getAccessToken(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userDao.getUserAuthToken(accessToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "'User has not signed in");
        }
        return userAuthTokenEntity;
    }

    //CreateQuestion Service
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity){
                return userDao.createQuestion(questionEntity);
    }

    //Get Question By UUID
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionByUserId(final String uuid) throws UserNotFoundException {
        QuestionEntity questionEntity=userDao.getQuestionByUserId(uuid);
        if (questionEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionEntity;
    }
}
