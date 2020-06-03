package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.service.business.DeleteQuestionService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class QuestionController {

  @Autowired
  private
  DeleteQuestionService deleteQuestionService;

  /**
   * @param questionId    the first {@code String} to delete a particular question.
   * @param authorization the second {@code String} to check if the access is available.
   * @return ResponseEntity is returned with Status OK.
   */
  @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
      @PathVariable("questionId") final String questionId,
      @RequestHeader("authorization") final String authorization)
      throws AuthorizationFailedException, InvalidQuestionException {

    // Delete requested question
    deleteQuestionService.deleteQuestion(questionId, authorization);

    // Return response
    QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(questionId)
        .status("QUESTION DELETED");
    return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
  }


}
