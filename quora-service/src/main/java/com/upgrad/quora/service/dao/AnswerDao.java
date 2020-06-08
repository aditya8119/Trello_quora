package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

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

  //Edit Answer DAO
  public AnswerEntity editAnswer(AnswerEntity answer) {
    entityManager.persist(answer);
    return answer;
  }

  //Delete Answer By UUID
  public void deleteAnswer(final String uuid) {
    AnswerEntity answerEntity = getAnswerByUUId(uuid);
    entityManager.remove(answerEntity);
  }

  //To get All Answers by question ID
  public List<AnswerEntity> getAllAnswersToQuestion(String questionId){
    try {
      return entityManager.createNamedQuery("answerByQuesId", AnswerEntity.class)
              .setParameter("questionId", questionId).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  //To check the answer belongs to particular user Id
  public AnswerEntity checkAnswerBelongToUser(String auuid, String uuuid) {

    try {
      return entityManager.createNamedQuery("checkAnswerBelongToUser", AnswerEntity.class)
              .setParameter("auuid", auuid)
              .setParameter("uuuid",uuuid)
              .getSingleResult();
    }catch (NoResultException nre)
    {
      return null;
    }
  }

  //To update the answer
  public AnswerEntity updateAnswer(AnswerEntity answerEntity)
  {
    return entityManager.merge(answerEntity);
  }

}
