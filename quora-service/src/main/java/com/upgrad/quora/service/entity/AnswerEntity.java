package com.upgrad.quora.service.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "answer")
public class AnswerEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "ans")
    private String ans;
    @Column(name = "date")
    private ZonedDateTime date;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userid;
    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity questionid;

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

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public UserEntity getUserid() {
        return userid;
    }

    public void setUserid(UserEntity userid) {
        this.userid = userid;
    }

    public QuestionEntity getQuestionid() {
        return questionid;
    }

    public void setQuestionid(QuestionEntity questionid) {
        this.questionid = questionid;
    }
}
