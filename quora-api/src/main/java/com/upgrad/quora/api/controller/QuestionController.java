package com.upgrad.quora.api.controller;

import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest createQuestionRequest,@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        String [] bearerToken = authorization.split("Bearer ");
        System.out.println("Bearer Token "+bearerToken[1]);
        UserAuthTokenEntity userAuthToken=questionService.getAccessToken(bearerToken[1]);
        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(createQuestionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setUser(userAuthToken.getUser());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);
        final QuestionEntity questionEntityResponse = questionService.createQuestion(questionEntity);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntityResponse.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }
}

