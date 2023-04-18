package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnswerDAO {

    @PersistenceContext
    EntityManager entityManager;

    public AnswerEntity create(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    @Transactional
    public void update(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
    }

    public AnswerEntity getEntity(final String uuid) {

        TypedQuery<AnswerEntity> query = entityManager.createQuery("SELECT u from AnswerEntity u where u.uuid = :uuid",AnswerEntity.class);
        List<AnswerEntity> list = query.setParameter("uuid",uuid).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    @Transactional
    public void delete(final AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    public List<AnswerEntity> getAllAnswers(final QuestionEntity questionEntity) {

        TypedQuery<AnswerEntity> query = entityManager.createQuery("SELECT u from AnswerEntity u",AnswerEntity.class);
        List<AnswerEntity> list = query.getResultList();
        List<AnswerEntity> listAns = new ArrayList<>();

        for (int i=0;i<list.size();i++) {
            if (list.get(i).getQuestionid() == questionEntity) {
                listAns.add(list.get(i));
            }
        }

        return listAns;
    }

}
