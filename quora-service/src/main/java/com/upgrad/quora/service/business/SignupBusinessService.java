package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * Signup Business Service to signup new user
     * @param userEntity UserEntity object containing details of the user
     * @return UserEntity
     * @throws SignUpRestrictedException SGR-001 Try any other Username, this Username has already been taken, SGR-002 his user has already been registered, try with any other emailId
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        if (userDao.getUserByUsername(userEntity.getUsername()) == null) {

            if (userDao.getUserByEmail(userEntity.getEmail()) == null) {
                String password = userEntity.getPassword();
                String[] encryptedText = cryptographyProvider.encrypt(password);
                userEntity.setSalt(encryptedText[0]);
                System.out.println("SignupBusinessService:SALT IS :"+encryptedText[0]);
                userEntity.setPassword(encryptedText[1]);
                System.out.println("SignupBusinessService:ENCRYPTED PASSWORD IS :"+encryptedText[1]);
                return userDao.createUser(userEntity);
            } else {
                throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
            }
        } else {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
    }
}
