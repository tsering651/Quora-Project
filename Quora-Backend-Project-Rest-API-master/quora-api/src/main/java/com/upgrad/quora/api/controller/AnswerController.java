package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.*;

@RestController
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create")
    public ResponseEntity<AnswerResponse> createAnswerOfQuestion(final AnswerRequest answerRequest, @PathVariable(name = "questionId")final String questionId, @RequestHeader("authorization")final String authorization) {

        String code;
        String message;

        try {
            AnswerEntity answerEntity = new AnswerEntity();
            ZonedDateTime now = ZonedDateTime.now();
            answerEntity.setDate(now);
            answerEntity.setUuid(UUID.randomUUID().toString());
            answerEntity.setAns(answerRequest.getAnswer());
            answerEntity = answerService.saveAnswer(answerEntity,questionId,authorization);
            code = answerEntity.getUuid();
            message = "ANSWER CREATED";
        }
        catch (InvalidQuestionException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        AnswerResponse answerResponse = new AnswerResponse().id(code).status(message);
        return new ResponseEntity<AnswerResponse>(answerResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,path = "/answer/edit/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(final AnswerEditRequest answerEditRequest,@PathVariable(name = "answerId")final String answerId,@RequestHeader("authorization")final String authorization) {

        String code;
        String message;

        try {
            AnswerEntity answerEntity = answerService.updateAnswer(answerEditRequest.getContent(),answerId,authorization);
            code = answerEntity.getUuid();
            message = "ANSWER EDITED";
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (AnswerNotFoundException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(code).status(message);
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable(name = "answerId")final String answerId, @RequestHeader("authorization")final String authorization) {

        String code;
        String message;

        try {
            answerService.deleteAnswer(answerId,authorization);
            code = answerId;
            message = "ANSWER DELETED";
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }
        catch (AnswerNotFoundException e) {
            code = e.getCode();
            message = e.getErrorMessage();
        }

        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(code).status(message);
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Map> getAllAnswers(@PathVariable(name = "questionId")final String questionId,@RequestHeader("authorization")final String authorization) {

        String code = null;
        String message = null;
        Boolean flag = false;
        List<AnswerEntity> list = null;

        try {
            list = answerService.getAllAnswers(questionId,authorization);
        }
        catch (AuthorizationFailedException e) {
            code = e.getCode();
            message = e.getErrorMessage();
            flag = true;
        }
        catch (InvalidQuestionException e) {
            code = e.getCode();
            message = e.getErrorMessage();
            flag = true;
        }

        if(!flag) {
            Map<String, List<AnswerDetailsResponse>> map = new HashMap<>();
            List<AnswerDetailsResponse> listQ = new ArrayList<>();

            for (int i=0;i<list.size();i++) {
                AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse() ;
                answerDetailsResponse.setAnswerContent(list.get(i).getAns());
                answerDetailsResponse.setId(list.get(i).getUuid());
                answerDetailsResponse.setQuestionContent(list.get(i).getQuestionid().getContent());
                listQ.add(answerDetailsResponse);
            }

            map.put("Response",listQ);
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
