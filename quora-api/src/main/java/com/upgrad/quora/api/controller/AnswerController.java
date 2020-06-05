package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.type.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class AnswerController {

  @Autowired
  AnswerService answerService;

  @Autowired
  private AuthorizationService authorizationService;

  /**
   * Api for creating an answer
   *
   * @param answerRequest
   * @param questionId
   * @param authorization
   * @return
   * @throws AuthorizationFailedException
   * @throws InvalidQuestionException
   */
  @PostMapping(path = "/question/{questionId}/answer/create", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, InvalidQuestionException {

    final AnswerEntity answerEntity = new AnswerEntity();
    answerEntity.setAnswer(answerRequest.getAnswer());

    // Return response with created answer entity
    final AnswerEntity createdAnswerEntity =
        answerService.createAnswer(answerEntity, questionId, authorization);
    AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid())
        .status("ANSWER CREATED");
    return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
  }

  /**
   * Api for creating an answer
   *
   * @param answerId
   * @param authorization
   * @return
   * @throws AuthorizationFailedException
   * @throws AnswerNotFoundException
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
          @PathVariable("answerId") final String answerId,
          @RequestHeader("authorization") final String authorization)
          throws AuthorizationFailedException, AnswerNotFoundException {

    // Delete requested answer
    answerService.deleteAnswer(answerId, authorization);

    // Return response
    com.upgrad.quora.api.model.AnswerDeleteResponse answerDeleteResponse = new com.upgrad.quora.api.model.AnswerDeleteResponse().id(answerId)
            .status("ANSWER DELETED");
    return new ResponseEntity<com.upgrad.quora.api.model.AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization")final String authorization)throws AuthorizationFailedException, InvalidQuestionException{
    final List<AnswerEntity> answerResponseEntityList = answerService.getAllAnswersToQuestion(questionId,authorization);
    List<AnswerDetailsResponse> answerOutputList = new ArrayList<>();
    for(AnswerEntity answerEntity:answerResponseEntityList)
    {
      AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse().id(answerEntity.getUuid()).questionContent(answerEntity.getQuestion().getContent()).answerContent(answerEntity.getAnswer());
      answerOutputList.add(answerDetailsResponse);
    }

    return new ResponseEntity<>(answerOutputList, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<?> editAnswerContent(AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerUuId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
    UserAuthTokenEntity userAuthTokenEntity = authorizationService.isValidActiveAuthToken(authorization, ActionType.EDIT_ANSWER);
    AnswerEntity answer = answerService.isUserAnswerOwner(answerUuId, userAuthTokenEntity, ActionType.EDIT_ANSWER);
    answer.setAnswer(answerEditRequest.getContent());
    answer.setDate(ZonedDateTime.now());
    AnswerEntity editedAnswer = answerService.editAnswer(answer);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(answerUuId).status("ANSWER EDITED");
    return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
  }

}
