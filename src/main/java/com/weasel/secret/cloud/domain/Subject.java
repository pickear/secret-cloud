package com.weasel.secret.cloud.domain;

import javax.persistence.*;
import java.util.List;

/**密码主体，如招行，淘宝
 * Created by Dylan on 2017/11/11.
 */
@Entity
@Table(name = "subject")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    /**
     * 标题
     */
    @Column(name = "title",nullable = false)
    private String title;
    /**
     * 主本地址，如http://www.taobao.com
     */
    @Column(name = "url",nullable = true)
    private String url;
    /**
     * 密码，如:
     * 支付密码:42234324
     * 登录密码:dsf24234234
     */
    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "subject_id")
    private List<Secret> secrets;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Secret> getSecrets() {
        return secrets;
    }

    public void setSecrets(List<Secret> secrets) {
        this.secrets = secrets;
    }


}
