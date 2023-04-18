package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class UserAuthDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public UserAuthEntity create(final UserAuthEntity newToken){
        entityManager.persist(newToken);
        return newToken;
    }

    public UserAuthEntity checkToken(final String accessToken) {

        TypedQuery<UserAuthEntity> query = entityManager.createQuery("SELECT u from UserAuthEntity u where u.accesstoken = :accesstoken",UserAuthEntity.class);
        List<UserAuthEntity> list = query.setParameter("accesstoken",accessToken).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }

    }

    public Boolean checkSign(final String accessToken) {

        TypedQuery<UserAuthEntity> query = entityManager.createQuery("SELECT u from UserAuthEntity u where u.accesstoken = :accesstoken",UserAuthEntity.class);
        List<UserAuthEntity> list = query.setParameter("accesstoken",accessToken).getResultList();
        if(list.size() !=0 && list.get(list.size()-1).getLogoutat() == null) {
            return true;
        }
        else {
            return false;
        }

    }

    @Transactional
    public UserAuthEntity update(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
}
