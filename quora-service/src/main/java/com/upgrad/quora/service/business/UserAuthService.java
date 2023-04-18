package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDAO;
import com.upgrad.quora.service.dao.UserDAO;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UserAuthService {

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;
    @Autowired
    UserDAO userDAO;
    @Autowired
    UserAuthDAO userAuthDAO;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username,final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDAO.getUserByEmail(username);
        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        else {
            String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
            if(userEntity.getPassword().equals(encryptedPassword)) {
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                UserAuthEntity userAuthEntity = new UserAuthEntity();
                userAuthEntity.setUserid(userEntity);
                ZonedDateTime now = ZonedDateTime.now();
                ZonedDateTime expiry = now.plusHours(8);
                userAuthEntity.setLoginat(now);
                userAuthEntity.setExpiresat(expiry);
                userAuthEntity.setUuid(userEntity.getUuid());
                String accessToken = jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiry);
                userAuthEntity.setAccesstoken(accessToken);
                userAuthDAO.create(userAuthEntity);
                userDAO.updateUser(userEntity);
                return userAuthEntity;
            }
            else {
                throw new AuthenticationFailedException("ATH-002","Password failed");
            }
        }
    }

    public UserAuthEntity authenticateToken(final String accessToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        if(userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }
        else {
            userAuthEntity.setLogoutat(ZonedDateTime.now());
            userAuthEntity = userAuthDAO.update(userAuthEntity);
            return userAuthEntity;
        }
    }

    public String getUuidByToken(final String accessToken) {
        UserAuthEntity userAuthEntity = userAuthDAO.checkToken(accessToken);
        return userAuthEntity.getUuid();
    }
}
