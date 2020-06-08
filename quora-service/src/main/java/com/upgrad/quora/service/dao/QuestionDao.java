package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

  @PersistenceContext
  private EntityManager entityManager;


  //Create Question DAO
  public QuestionEntity createQuestion(QuestionEntity questionEntity) {
    entityManager.persist(questionEntity);
    return questionEntity;
  }

  //To get All the questions posted by a particular user ID
  public List<QuestionEntity> getQuestionByUserId(final String uuid) {
    try {
      System.out.println("User Id: " + uuid);
      return entityManager.createNamedQuery("questionByUserId", QuestionEntity.class)
          .setParameter("uuid", uuid).getResultList();
    } catch (NoResultException nre) {
      return null;
    }
  }

  //GET Question By Question UUID
  public QuestionEntity getQuestionByQUuid(final String uuid) {
    try {
      return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class)
          .setParameter("uuid", uuid).getSingleResult();
    } catch (NoResultException nre) {
      return null;
    }
  }

  //To delete a question
  public void deleteQuestion(final String uuid) {
    QuestionEntity questionEntity = getQuestionByQUuid(uuid);
    entityManager.remove(questionEntity);
  }

  //To get All questions
  public List<QuestionEntity> getAllQuestions(){
    try {
      return entityManager.createNamedQuery("allQuestions", QuestionEntity.class).getResultList();

    }catch (NoResultException nre){
      return null;
    }
  }

  //To edit a question
  public QuestionEntity editQuestion(QuestionEntity question) {
    entityManager.persist(question);
    return question;
  }

}
