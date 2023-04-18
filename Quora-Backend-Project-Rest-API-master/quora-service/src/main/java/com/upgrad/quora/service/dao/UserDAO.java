package com.upgrad.quora.service.dao;


import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public UserEntity createUser(UserEntity userEntity) {

        try {
            entityManager.persist(userEntity);
        }
        catch (Exception e) {

            System.out.println(e);
        }

        return userEntity;

    }

    public Boolean checkUserName(String userName) {

        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.userName = :userName",UserEntity.class);
        List<UserEntity>  list = query.setParameter("userName",userName).getResultList();
        if(list.size() == 0) {
            return true;
        }
        else {
            return false;
        }

    }

    public Boolean checkEmail(String email) {

        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.emailAddress = :emailAddress",UserEntity.class);
        List<UserEntity>  list = query.setParameter("emailAddress",email).getResultList();
        if(list.size() == 0) {
            return true;
        }
        else {
            return false;
        }

    }

    public UserEntity getUserByEmail(final String username) {
        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.userName = :userName",UserEntity.class);
        List<UserEntity> list = query.setParameter("userName",username).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    public UserEntity checkUuid(final String uuid) {
        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.uuid = :uuid",UserEntity.class);
        List<UserEntity> list = query.setParameter("uuid",uuid).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    public Boolean checkRole(final String uuid) {

        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.uuid = :uuid",UserEntity.class);
        List<UserEntity> list = query.setParameter("uuid",uuid).getResultList();
        if(list.size() != 0 && list.get(0).getRole().equals("admin")) {
            return true;
        }
        else {
            return false;
        }
    }

    @Transactional
    public Boolean deleteUser(final UserEntity userEntity) {
        entityManager.remove(userEntity);
        return true;
    }

    public UserEntity getUserByUuid(final String uuid) {

        TypedQuery<UserEntity> query = entityManager.createQuery("SELECT u from UserEntity u where u.uuid = :uuid",UserEntity.class);
        List<UserEntity> list = query.setParameter("uuid",uuid).getResultList();
        if(list.size() == 0) {
            return null;
        }
        else {
            return list.get(0);
        }
    }

    public void updateUser(UserEntity updatedUser) {
        entityManager.merge(updatedUser);
    }

}
