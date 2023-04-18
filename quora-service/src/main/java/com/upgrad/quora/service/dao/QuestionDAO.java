package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDAO {

    @PersistenceContext
    EntityManager entityManager;

    public QuestionEntity createQuestion(final String accessToken,final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<String> getAllQuestions(final UserEntity userEntity) {

        TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT u from QuestionEntity u where u.userId = :userId",QuestionEntity.class);
        List<QuestionEntity> list = query.setParameter("userId",userEntity).getResultList();
        List<String> listQuestions = new ArrayList<>();

        for(int i=0;i<list.size();i++) {
            listQuestions.add(list.get(i).getContent());
        }

        return listQuestions;
    }

    public QuestionEntity getEntity(final String uuid) {

        TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT u from QuestionEntity u where u.uuid = :uuid",QuestionEntity.class);
        List<QuestionEntity> list = query.setParameter("uuid",uuid).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    @Transactional
    public QuestionEntity updateQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    @Transactional
    public void deleteQuestion(final QuestionEntity questionEntity) {
        entityManager.remove(questionEntity);
    }

    public List<String> getAllQuestionsByEntity(final UserEntity userEntity) {

        List<String> listQuestions = new ArrayList<>();
        TypedQuery<QuestionEntity> query = entityManager.createQuery("SELECT u from QuestionEntity u",QuestionEntity.class);
        List<QuestionEntity> list = query.getResultList();

        for (int i=0;i<list.size();i++) {
            if(list.get(i).getUserId() == userEntity) {
                listQuestions.add(list.get(i).getContent());
            }
        }

        return listQuestions;
    }

    public void updateUser(QuestionEntity questionEntity) {
        entityManager.merge(questionEntity);
    }

}
