package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.UserService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CommonController {

    @Autowired
    UserService userService;

    @RequestMapping(method = RequestMethod.GET,path = "user/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map> getUser(@PathVariable(name = "userId")final String uuid, @RequestHeader("authorization")final String authorization) {

        String message = null;
        String code = null;
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        Boolean flag = false;

        try {
            UserEntity userEntity = userService.getUser(uuid, authorization);
            userDetailsResponse.setAboutMe(userEntity.getAboutMe());
            userDetailsResponse.setContactNumber(userEntity.getContactNumber());
            userDetailsResponse.setCountry(userEntity.getCountry());
            userDetailsResponse.setDob(userEntity.getDob());
            userDetailsResponse.setEmailAddress(userEntity.getEmailAddress());
            userDetailsResponse.setFirstName(userEntity.getFirstName());
            userDetailsResponse.setLastName(userEntity.getLastName());
            userDetailsResponse.setUserName(userEntity.getUserName());
            message = "User details found.";
            code = userEntity.getUuid();
        }
        catch (UserNotFoundException e) {
            flag = true;
            message = e.getErrorMessage();
            code = e.getCode();
        }
        catch (AuthorizationFailedException e) {
            flag = true;
            message = e.getErrorMessage();
            code = e.getCode();
        }

        if(!flag) {

            Map<String,UserDetailsResponse> map = new HashMap<>();
            map.put("Response",userDetailsResponse);
            return new ResponseEntity<Map>(map, HttpStatus.OK);
        }
        else {
            Map<String,String> map = new HashMap<>();
            map.put("id",code);
            map.put("status",message);
            return new ResponseEntity<Map>(map,HttpStatus.OK);
        }
    }

}
