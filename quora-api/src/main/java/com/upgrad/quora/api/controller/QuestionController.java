package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
}

