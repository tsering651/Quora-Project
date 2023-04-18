package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserAuthService;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    UserAuthService userAuthService;

    @RequestMapping(path = "/user/signup",method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) {

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmailAddress(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setUuid(UUID.randomUUID().toString());
        String message;
        String code;
        try {
            userEntity = userService.signUp(userEntity);
            message = "USER SUCCESSFULLY REGISTERED";
            code = userEntity.getUuid();
        }
        catch (SignUpRestrictedException e) {
            message = e.getErrorMessage();
            code = e.getCode();
        }
        SignupUserResponse response = new SignupUserResponse();
        response.setId(code);
        response.setStatus(message);
        return new ResponseEntity<SignupUserResponse>(response,HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST,path = "/user/signin",produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authentication")final String authentication) {

        byte[] decoded = Base64.getDecoder().decode(authentication);
        String decodedText = new String(decoded);
        String[] decodedArray = decodedText.split(":");
        String id;
        String message;
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            UserAuthEntity userAuthEntity = userAuthService.authenticate(decodedArray[0], decodedArray[1]);
            id = userAuthEntity.getUserid().getUuid();
            message = "SIGNED IN SUCCESSFULLY";
            httpHeaders.add("access-token",userAuthEntity.getAccesstoken());
        }
        catch (AuthenticationFailedException e) {
            id = e.getCode();
            message = e.getErrorMessage();
        }

        SigninResponse signinResponse = new SigninResponse();
        signinResponse.setId(id);
        signinResponse.setMessage(message);

        return new ResponseEntity<SigninResponse>(signinResponse,httpHeaders,HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST,path = "/user/signout",produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authentication")final String authentication) {

        String message;
        String code;

        try {
            UserAuthEntity userAuthEntity = userAuthService.authenticateToken(authentication);
            SignoutResponse signoutResponse = new SignoutResponse();
            message = "SIGNED OUT SUCCESSFULLY";
            code = userAuthEntity.getUuid();
        }
        catch (SignOutRestrictedException e) {
            message = e.getErrorMessage();
            code = e.getCode();
        }

        SignoutResponse signoutResponse = new SignoutResponse();
        signoutResponse.setMessage(message);
        signoutResponse.setId(code);
        return new ResponseEntity<SignoutResponse>(signoutResponse,HttpStatus.OK);

    }

}
