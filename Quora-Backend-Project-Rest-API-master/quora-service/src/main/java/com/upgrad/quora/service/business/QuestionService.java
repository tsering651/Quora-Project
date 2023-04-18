package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDAO;
import com.upgrad.quora.service.dao.UserAuthDAO;
import com.upgrad.quora.service.dao.UserDAO;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    UserAuthDAO userAuthDAO;
    @Autowired
    UserDAO userDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final String accessToken,final QuestionEntity questionEntity) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userDAO.getUserByUuid(userAuthEntity.getUuid());
                questionEntity.setUserId(userEntity);
                QuestionEntity questionEntityNew = questionDAO.createQuestion(accessToken,questionEntity);
                userDAO.updateUser(userEntity);
                return questionEntityNew;
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public List<String> getAllQuestions(final String accessToken) throws AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userAuthEntity.getUserid();
                List<String> listQuestions = questionDAO.getAllQuestions(userEntity);
                return listQuestions;
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

    }

    public QuestionEntity editQuestion(final String content,final String questionId,final String accessToken) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                QuestionEntity questionEntity = questionDAO.getEntity(questionId);
                if(questionEntity != null) {
                    if (userAuthEntity.getUserid() == questionEntity.getUserId()) {
                        questionEntity.setContent(content);
                        questionEntity = questionDAO.updateQuestion(questionEntity);
                        return questionEntity;
                    } else {
                        throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
                    }
                }
                else {
                    throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit the question");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public void deleteQuestion(final String uuid,final String accessToken) throws AuthorizationFailedException,InvalidQuestionException{

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userDAO.getUserByUuid(userAuthEntity.getUuid());
                QuestionEntity questionEntity = questionDAO.getEntity(uuid);
                if(questionEntity != null) {
                    if (userEntity.getRole().equals("admin") || userEntity == questionEntity.getUserId()) {
                        questionDAO.deleteQuestion(questionEntity);
                    }
                    else {
                        throw new AuthorizationFailedException("ATHR-003","Only the question owner or admin can delete the question");
                    }
                }
                else {
                    throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to delete a question");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }

    }

    public List<String> getAllQuestionsOfAnotherUser(final String uuid,final String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if (userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userDAO.getUserByUuid(uuid);
                if(userEntity != null) {
                    return questionDAO.getAllQuestionsByEntity(userEntity);
                }
                else {
                    throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

}
