package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_auth")
public class UserAuthEntity {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "uuid")
    @NotNull
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userid;

    @Column(name = "access_token")
    private String accesstoken;
    @Column(name = "expires_at")
    private ZonedDateTime expiresat;
    @Column(name = "login_at")
    private ZonedDateTime loginat;
    @Column(name = "logout_at")
    private ZonedDateTime logoutat;

    public ZonedDateTime getExpiresat() {
        return expiresat;
    }

    public void setExpiresat(ZonedDateTime expiresat) {
        this.expiresat = expiresat;
    }

    public ZonedDateTime getLoginat() {
        return loginat;
    }

    public void setLoginat(ZonedDateTime loginat) {
        this.loginat = loginat;
    }

    public ZonedDateTime getLogoutat() {
        return logoutat;
    }

    public void setLogoutat(ZonedDateTime logoutat) {
        this.logoutat = logoutat;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public UserEntity getUserid() {
        return userid;
    }

    public void setUserid(UserEntity userid) {
        this.userid = userid;
    }
}
