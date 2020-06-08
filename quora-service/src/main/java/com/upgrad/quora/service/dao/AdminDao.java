package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Repository
public class AdminDao {

    @PersistenceContext
    private EntityManager entityManager;

    //To delete the user By UUID
    public String deleteUser(final String uuid) throws UserNotFoundException {
        try {
            UserEntity deletedUserEntity = entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid).getSingleResult();
            Integer deletedUserId = deletedUserEntity.getId();
            String deletedUserUuid = uuid;
            entityManager.remove(deletedUserEntity);
            return deletedUserUuid;
        } catch (NullPointerException exc) {
            return null;
        }
    }

}