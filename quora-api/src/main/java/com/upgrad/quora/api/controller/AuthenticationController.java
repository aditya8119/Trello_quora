package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.upgrad.quora.api.model.SigninResponse;

import java.util.Base64;

@RestController
@RequestMapping("/")
public class AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;

    //Sign In API
    @RequestMapping(method = RequestMethod.POST, path = "user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {


        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        System.out.println("Username : "+decodedArray[0].substring(1));
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0].substring(1), decodedArray[1].substring(0,decodedArray[1].length()-1));
        UserEntity user = userAuthToken.getUser();

        SigninResponse authorizedUserResponse = new com.upgrad.quora.api.model.SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }
}
