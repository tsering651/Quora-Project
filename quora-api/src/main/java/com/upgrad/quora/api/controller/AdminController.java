package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.DELETE,path = "/admin/user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable("userId") final String uuid,@RequestHeader("authorization") final String authorization) {

        String message;
        String code;

        try {
            userService.deleteUser(uuid, authorization);
            message = "USER SUCCESSFULLY DELETED";
            code = uuid;

        }
        catch (UserNotFoundException e) {
            message = e.getErrorMessage();
            code = e.getCode();
        }
        catch (AuthorizationFailedException e) {
            message = e.getErrorMessage();
            code = e.getCode();
        }

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(code).status(message);
        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }

}
