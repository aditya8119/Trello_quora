package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.SignupBusinessService;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/user/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("1234abc");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");

        final UserEntity createdUserEntity = signupBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse,HttpStatus.CREATED);
    }

    //Sign In API
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<com.upgrad.quora.api.model.SigninResponse> signin(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {


        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");
        System.out.println("Username : "+decodedArray[0].substring(1));
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0].substring(1), decodedArray[1].substring(0,decodedArray[1].length()-1));
        UserEntity user = userAuthToken.getUser();

        com.upgrad.quora.api.model.SigninResponse authorizedUserResponse = new com.upgrad.quora.api.model.SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<com.upgrad.quora.api.model.SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }

   /* @RequestMapping(method = RequestMethod.POST, path = "/user/signout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<com.upgrad.quora.api.model.SignoutResponse> logout(
            @RequestHeader("authorization") final String authorization)
            throws SignOutRestrictedException, AuthenticationFailedException {

      //  String jwtToken = authenticationService.getBearerAccessToken(authorization);

      //  UserAuthTokenEntity userAuthEntity = authenticationService.logoff(jwtToken);
        UserEntity user = userAuthEntity.getUser();

        com.upgrad.quora.api.model.SignoutResponse signoutResponse = new com.upgrad.quora.api.model.SignoutResponse().id(user.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthEntity.getAccessToken());
        return new ResponseEntity<com.upgrad.quora.api.model.SignoutResponse>(signoutResponse, headers, HttpStatus.OK);
    }*/

}
