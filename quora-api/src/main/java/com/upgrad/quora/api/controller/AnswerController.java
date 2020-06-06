package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.business.SignoutService;
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

  @Autowired
  private SignoutService signoutService;

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

  /**
   * This API edits the answer which already exist in the database.
   *
   * @param authorization To authenticate the user who is trying to edit the answer.
   * @param answerId Id of the answe which is to be edited.
   * @param answerEditRequest Contains the new content of the answer.
   * @return ResponseEntity
   * @throws AuthorizationFailedException ATHR-001 If the user has not signed in and ATHR-002 If the
   *     user is already signed out and ATHR-003 if the user is not the owner of the answer.
   * @throws AnswerNotFoundException ANS-001 if the answer is not found in the database.
   */
  @RequestMapping(method = RequestMethod.PUT,
          path = "/answer/edit/{answerId}",
          consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
          produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<AnswerEditResponse> editAnswerContent(
          final AnswerEditRequest answerEditRequest,
          @PathVariable("answerId") final String answerId,
          @RequestHeader("authorization") final String authorization)
          throws AuthorizationFailedException, AnswerNotFoundException {

    UserAuthTokenEntity userAuthTokenEntity = signoutService.getUser(authorization);

    AnswerEntity answerEntity = answerService.getAnswerFromId(answerId);
    AnswerEntity checkedAnswer = answerService.checkAnswerBelongToUser(userAuthTokenEntity, answerEntity);
    checkedAnswer.setAnswer(answerEditRequest.getContent());
    AnswerEntity updatedAnswer = answerService.updateAnswer(checkedAnswer);
    AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(updatedAnswer.getUuid()).status("ANSWER EDITED");
    return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);
  }

}
