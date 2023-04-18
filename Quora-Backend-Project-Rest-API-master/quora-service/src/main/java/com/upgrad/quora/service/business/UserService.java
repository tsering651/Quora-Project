package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDAO;
import com.upgrad.quora.service.dao.UserDAO;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    UserAuthDAO userAuthDAO;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {

        if(userDAO.checkEmail(userEntity.getEmailAddress())) {
            if(userDAO.checkUserName(userEntity.getUserName())) {
                final String[] cryPasswordAndSalt = passwordCryptographyProvider.encrypt(userEntity.getPassword());
                userEntity.setPassword(cryPasswordAndSalt[1]);
                userEntity.setSalt(cryPasswordAndSalt[0]);

                userEntity = userDAO.createUser(userEntity);
            }
            else {
                throw new SignUpRestrictedException("'SGR-001","Try any other Username, this Username has already been taken");
            }
        }
        else {
            throw new SignUpRestrictedException("SGR-002","This user has already been registered, try with any other emailId");
        }
        return userEntity;
    }

    public UserEntity getUser(final String uuid,final String accessToken) throws UserNotFoundException, AuthorizationFailedException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if(userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userDAO.checkUuid(uuid);
                if (userEntity != null) {
                    return userEntity;
                }
                else {
                    throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

    public Boolean deleteUser(final String uuid,final String accessToken) throws UserNotFoundException,AuthorizationFailedException{

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity != null) {
            if(userAuthDAO.checkSign(accessToken)) {
                UserEntity userEntity = userDAO.checkUuid(uuid);
                if(userEntity != null) {
                    if (userDAO.checkRole(uuid)) {
                        return userDAO.deleteUser(userEntity);
                    } else {
                        throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Entered user is not an admin");
                    }
                }
                else {
                    throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
                }
            }
            else {
                throw new AuthorizationFailedException("ATHR-002","User is signed out");
            }
        }
        else {
            throw  new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
    }

}
