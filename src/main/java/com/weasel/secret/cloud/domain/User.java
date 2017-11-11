package com.weasel.secret.cloud.domain;

import javax.persistence.*;
import java.util.List;

/**用户
 * Created by Dylan on 2017/11/11.
 */
@Entity
@Table(name = "user")
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    /**
     * 用户名
     */
    @Column(name = "username",nullable = false,unique = true)
    private String username;
    /**
     * 密码
     */
    @Column(name = "password",nullable = false)
    private String password;
    /**
     * 邮箱
     */
    @Column(name = "email",nullable = true)
    private String email;
    /**
     * 密码主体，比如招行，淘宝
     */
    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id")
    private List<Subject> subjects;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
