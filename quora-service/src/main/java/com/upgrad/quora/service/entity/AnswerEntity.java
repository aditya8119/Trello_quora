package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer", schema = "public")
@NamedQueries({
        @NamedQuery(name = "answerByUuid", query = "select a from AnswerEntity a where a.uuid =:uuid"),
        @NamedQuery(name = "answerByQuesId", query = "select a from AnswerEntity a where a.question.uuid =:questionId"),
        @NamedQuery(name = "checkAnswerBelongToUser" , query = "select a from AnswerEntity a INNER JOIN UserEntity u on a.user = u.id where a.uuid =:auuid and u.uuid = :uuuid"),

})
public class AnswerEntity {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "uuid")
  @NotNull
  @Size(max = 200)
  private String uuid;

  @Column(name = "ans")
  @NotNull
  @Size(max = 255)
  private String answer;

  @Column(name = "date")
  @NotNull
  private ZonedDateTime date;

  @ManyToOne
  @JoinColumn(name = "user_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "question_id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  @NotNull
  private QuestionEntity question;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public QuestionEntity getQuestion() {
    return question;
  }

  public void setQuestion(QuestionEntity question) {
    this.question = question;
  }

}
