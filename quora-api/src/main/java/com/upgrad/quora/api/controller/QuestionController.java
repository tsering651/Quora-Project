package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAuthService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class QuestionController {

    @Autowired
    QuestionService questionService;
    @Autowired
    UserAuthService userAuthService;

    @RequestMapping(method = RequestMethod.POST,path = "/question/create",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization, final QuestionRequest questionRequest) {

        String code;
        String message;

        try {
            QuestionEntity questionEntity = new QuestionEntity();
            questionEntity.setContent(questionRequest.getContent());
            ZonedDateTime now = ZonedDateTime.now();
            questionEntity.setDate(now);
            questionEntity.setUuid(UUID.randomUUID().toString());
            questionEntity = questionService.createQuestion(authorization,questionEntity);
            code = questionEntity.getUuid();
            message = "QUESTION CREATED";
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        QuestionResponse questionResponse = new QuestionResponse().id(code).status(message);
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/question/all")
    public ResponseEntity<Map> getAllQuestions(@RequestHeader("authorization")final String authorization) {

        String meassge = null;
        String code = null;
        String uuid = null;
        Boolean flag = false;

        List<String> questionEntities = null;

        try {
            questionEntities = questionService.getAllQuestions(authorization);
            uuid = userAuthService.getUuidByToken(authorization);
        }
        catch (AuthorizationFailedException e) {
            flag = true;
            code = e.getCode();
            meassge = e.getErrorMessage();
        }

        if(!flag) {
            Map<String, List<String>> map = new HashMap<>();
            if (questionEntities.size() != 0) {
                map.put(uuid, questionEntities);
            }
            return new ResponseEntity<Map>(map, HttpStatus.OK);
        }
        else {
            Map<String,String> map = new HashMap<>();
            map.put("id",code);
            map.put("status",meassge);
            return new ResponseEntity<Map>(map,HttpStatus.OK);
        }

    }

    @RequestMapping(method = RequestMethod.PUT,path = "/question/edit/{questionId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(final QuestionEditRequest questionEditRequest, @PathVariable(name = "questionId") final String questionId, @RequestHeader("authorization")final String authorization) {

        String code;
        String message;
        try {
            QuestionEntity questionEntity = questionService.editQuestion(questionEditRequest.getContent(),questionId,authorization);
            code = questionEntity.getUuid();
            message = "QUESTION EDITED";
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (InvalidQuestionException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(code).status(message);
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/question/delete/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@PathVariable(name = "questionId")final String questionId,@RequestHeader("authorization")final String authorization) {

        String code;
        String message;

        try {
            questionService.deleteQuestion(questionId,authorization);
            code = questionId;
            message = "QUESTION DELETED";

        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (InvalidQuestionException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse().id(code).status(message);
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map> getAllQuestionsOfAnotherUser(@PathVariable(name = "userId")final String userId,@RequestHeader("authorization")final String authorization) {

        String code = null;
        String message = null;
        Boolean flag = false;
        List<String> listQuestions = null;

        try {
            listQuestions = questionService.getAllQuestionsOfAnotherUser(userId,authorization);
        }
        catch (AuthorizationFailedException e) {
            flag = true;
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (UserNotFoundException e) {
            flag = true;
            code = e.getCode();
            message = e.getErrorMessage();
        }

        if(!flag) {
            Map<String,List<String>> map = new HashMap<>();
            ArrayList<String> arr = new ArrayList<>();
            arr.add(userId);
            map.put("id",arr);
            map.put("Questions",listQuestions);
            return new ResponseEntity<Map>(map,HttpStatus.OK);
        }
        else {
            Map<String,String> map = new HashMap<>();
            map.put("id",code);
            map.put("status",message);
            return new ResponseEntity<Map>(map,HttpStatus.OK);
        }
    }
}
