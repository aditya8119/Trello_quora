package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.business.AuthorizationService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*This class implements the userDelete - "/admin/user/{userId}"*/
@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthorizationService authorizationService;

    /**
     * This method deletes a registered user
     * @param uuid UUID of the user
     * @param authorization Access Token
     * @returns ResponseEntity
     * @throws AuthorizationFailedException ATHR-001 User has not signed in
     * @throws UserNotFoundException ATHR-002 User is signed out.Sign in first to post a question
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String uuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, UserNotFoundException {

        String UUID = adminService.deleteUser(uuid, authorization);
        /*UserDeletResposnse will have the message that user has been successfully deleted*/
        final UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(UUID).status("USER SUCCESSFULLY DELETED");

        /*Returning the message in the JSON response with the corresponding HTTP status*/
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);

    }
}