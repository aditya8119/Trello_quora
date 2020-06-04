package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

  @PersistenceContext
  private EntityManager entityManager;

  public AnswerEntity createAnswer(AnswerEntity answerEntity) {
    entityManager.persist(answerEntity);
    return answerEntity;
  }

  //GET Answer By UUID
  public AnswerEntity getAnswerByUUId(final String uuid) {
    try {
      System.out.println("User Id: " + uuid);
      return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class)
              .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  //Delete Answer By UUID
  public void deleteAnswer(final String uuid) {
    AnswerEntity answerEntity = getAnswerByUUId(uuid);
    entityManager.remove(answerEntity);
  }

}
