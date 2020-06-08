package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.SignoutService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
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

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private SignoutService signoutService;


    /**
     * This method is used to create a question for the user Id
     *
     * @param createQuestionRequest Content of the question
     * @param authorization Access Token
     * @return responseEntity
     * @throws AuthorizationFailedException ATHR-001- 'User has not signed in' ,
         'ATHR-002' - 'User is signed out.Sign in first to post a question'
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest createQuestionRequest,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        final QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(createQuestionRequest.getContent());

        final QuestionEntity questionEntityResponse = questionService.createQuestion(questionEntity,authorization);
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntityResponse.getUuid()).status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * This method is used to get all questions posted by a user Id
     *
     * @param uuid UUID of the question
     * @param authorization Access Token
     * @return responseEntity
     * @throws AuthorizationFailedException ATHR-001- 'User has not signed in'
     * @throws UserNotFoundException 'ATHR-002' - 'User is signed out.Sign in first to get all questions posted by a specific user'
     */
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

    /**
     * This method is used to delete a question
     *
     * @param questionId UUID of the question
     * @param authorization Access Token
     * @return responseEntity
     * @throws AuthorizationFailedException ATHR-001- 'User has not signed in',
      'ATHR-002'-'User is signed out.Sign in first to delete a question',
      'ATHR-003'-'Only the question owner or admin can delete the question'
     * @throws InvalidQuestionException 'QUES-001' - 'Entered question uuid does not exist'
     */
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

    /**
     * This method is used to get All Questions posted by any user
     *
     * @param authorization Access Token
     * @return responseEntity
     * @throws AuthorizationFailedException ATHR-001- 'User has not signed in',
    'ATHR-002'-'User is signed out.Sign in first to get all questions'
     */
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

    /**
     * This method is used to edit a question that has been posted by a user.
     *
     * @param questionId        The uuid of the question
     * @param questionEditRequest The edited question details
     * @param authorization    Access Token
     * @return ResponseEntity
     * @throws AuthorizationFailedException ATHR-001- 'User has not signed in',
    'ATHR-002'-'User is signed out.Sign in first to edit the question',
    'ATHR-003'-'Only the question owner can edit the question'
     * @throws InvalidQuestionException     'QUES-001' - 'Entered question uuid does not exist'
     */
    @RequestMapping(method = RequestMethod.PUT,
            path = "/question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<QuestionEditResponse> editQuestionContent(
            @PathVariable("questionId") final String questionId,
            @RequestHeader("authorization") final String authorization,
            QuestionEditRequest questionEditRequest)
            throws AuthorizationFailedException,InvalidQuestionException {

        final UserAuthTokenEntity userAuthEntity = signoutService.getUser(authorization);
        String content = questionEditRequest.getContent();

        QuestionEntity editedQuestion = questionService.editQuestionContent(questionId,userAuthEntity, content);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }
}

