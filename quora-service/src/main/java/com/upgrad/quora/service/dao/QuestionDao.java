package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class QuestionDao {

  @PersistenceContext
  private EntityManager entityManager;

  public QuestionEntity getQuestionByQUuid(final String uuid) {
    try {
      return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  public void deleteQuestion(final String uuid) {
    QuestionEntity questionEntity = getQuestionByQUuid(uuid);
    entityManager.remove(questionEntity);
  }

}
