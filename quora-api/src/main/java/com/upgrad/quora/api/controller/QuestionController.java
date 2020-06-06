package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    private AuthorizationService authorizationService;


    //Create Question API
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest createQuestionRequest,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(createQuestionRequest.getContent());

        final QuestionEntity questionEntityResponse = questionService.createQuestion(questionEntity,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntityResponse.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    //Get Question By User Id
    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(@PathVariable("userId") final String uuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        final List<QuestionEntity> questionEntityResponseList = questionService.getQuestionByUserId(uuid,authorization);
        List<QuestionDetailsResponse> questionOutputList=new ArrayList<>();
        for(QuestionEntity quesEntity:questionEntityResponseList)
        {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(quesEntity.getUuid())
                    .content(quesEntity.getContent());
            questionOutputList.add(questionDetailsResponse);
        }

        return new ResponseEntity<>(questionOutputList, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
        @PathVariable("questionId") final String questionId,
        @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, InvalidQuestionException {

        // Delete requested question
        questionService.deleteQuestion(questionId, authorization);

        // Return response
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId)
            .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final List<QuestionEntity> questionEntityResponseList = questionService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> questionOutputList=new ArrayList<>();
        for(QuestionEntity quesEntity:questionEntityResponseList)
        {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(quesEntity.getUuid())
                    .content(quesEntity.getContent());
            questionOutputList.add(questionDetailsResponse);
        }

        return new ResponseEntity<>(questionOutputList, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> editQuestionContent(QuestionEditRequest questionEditRequest,
                                                 @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws
            AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization, ActionType.EDIT_QUESTION);
        QuestionEntity question = questionService.isUserQuestionOwner(questionId, userAuthTokenEntity, ActionType.EDIT_QUESTION);
        question.setContent(questionEditRequest.getContent());
        //edits the question
        questionService.editQuestion(question);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(question.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }
}

