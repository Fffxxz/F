package com.fenglin.springboottest.vo;

/**
 * 返回给前端的用户信息。
 *
 * <p>前端（AuthCard.vue）里用的是 {@code user.name}，而数据库列名是 {@code username}，
 * 因此这里刻意把字段命名为 {@code name} 完成一次「对齐翻译」，
 * 这样前端一行代码都不用改。</p>
 *
 * @author fenglin
 */
public class UserVO {

    private Long id;

    /** 对应数据库 user.username，前端读作 name */
    private String name;

    private String email;

    public UserVO() {
    }

    public UserVO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
