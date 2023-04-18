package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDAO;
import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserAuthDAO;
import com.upgrad.quora.service.dao.UserDAO;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    UserAuthDAO userAuthDAO;
    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    AnswerDAO answerDAO;
    @Autowired
    UserDAO userDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity saveAnswer(final AnswerEntity answerEntity,final String questionId,final String accessToken) throws InvalidQuestionException, AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                QuestionEntity questionEntity = questionDAO.getEntity(questionId);
                if(questionEntity != null) {
                    answerEntity.setQuestionid(questionEntity);
                    answerEntity.setUserid(userAuthEntity.getUserid());
                    answerDAO.create(answerEntity);
                    userDAO.updateUser(questionEntity.getUserId());
                    questionDAO.updateUser(questionEntity);
                    return answerEntity;
                }
                else {
                    throw new InvalidQuestionException("QUES-001","The question entered is invalid");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post an answer");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public AnswerEntity updateAnswer(final String content,final String answerId,final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                AnswerEntity answerEntity = answerDAO.getEntity(answerId);
                if(answerEntity != null) {
                    if (answerEntity.getUserid() == userAuthEntity.getUserid()) {
                        answerEntity.setAns(content);
                        answerDAO.update(answerEntity);
                        return answerEntity;
                    }
                    else {
                        throw new AuthorizationFailedException("ATHR-003","Only the answer owner can edit the answer");
                    }
                }
                else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit an answer");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public void deleteAnswer(final String answerId,final String accessToken) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                AnswerEntity answerEntity = answerDAO.getEntity(answerId);
                if(answerEntity != null) {
                    if (answerEntity.getUserid() == userAuthEntity.getUserid() || userAuthEntity.getUserid().getRole().equals("admin")) {
                        answerDAO.delete(answerEntity);
                    }
                    else {
                        throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
                    }
                }
                else {
                    throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete an answer");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public List<AnswerEntity> getAllAnswers(final String questionId,final String accessToken) throws AuthorizationFailedException,InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                QuestionEntity questionEntity = questionDAO.getEntity(questionId);
                if(questionEntity != null) {
                    List<AnswerEntity> list = answerDAO.getAllAnswers(questionEntity);
                    return list;
                }
                else {
                    throw new InvalidQuestionException("QUES-001","The question with entered uuid whose details are to be seen does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get the answers");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

}
