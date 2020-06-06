package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.PasswordCryptographyProvider;
import com.upgrad.quora.service.business.SignoutService;
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
    AuthenticationService authenticationService;

    @Autowired
    SignoutService signoutService;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

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
        System.out.println("Username :"+decodedArray[0]);
        System.out.println("Password :"+decodedArray[1]);
        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuthToken.getUser();

        com.upgrad.quora.api.model.SigninResponse authorizedUserResponse = new com.upgrad.quora.api.model.SigninResponse().id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<SigninResponse>(authorizedUserResponse, headers, HttpStatus.OK);
    }


    /**
     * This method exposes endpoint to signout a user in the Quora Application
     *
     * @param authorization The signout user request details
     * @return ResponseEntity
     * @throws SignOutRestrictedException This exception is thrown if either given username or email address already exists in the application
     */
    @RequestMapping(method = RequestMethod.POST,
            path = "/user/signout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(
            @RequestHeader("authorization") final String authorization)
            throws SignOutRestrictedException {
        String userUUID = signoutService.signOut(authorization);

        SignoutResponse signoutResponse = new SignoutResponse().id(userUUID)
                .message("SIGNED OUT SUCCESSFULLY");

        HttpHeaders headers = new HttpHeaders();
        headers.add("user-uuid", userUUID);

        return new ResponseEntity<SignoutResponse>(signoutResponse, headers, HttpStatus.OK);
    }

}
