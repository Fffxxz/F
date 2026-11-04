package com.fenglin.springboottest.common;

import com.fenglin.springboottest.entity.User;
import com.fenglin.springboottest.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/** 首次启动且表为空时，初始化种子用户（熊大 / 熊二） */
@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) {
        if (userMapper.selectCount(null) == 0) {
            insert("熊大", "admin@boonie.com", "bear123456", 0);
            insert("熊二", "bear2@boonie.com", "bear123456", 1);
            System.out.println("🌲 已初始化种子用户：熊大（管理员）/ 熊二（普通用户）");
        }
    }

    private void insert(String name, String email, String pwd, Integer role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(pwd);
        u.setRole(role);
        u.setCreatedAt(LocalDateTime.now());
        userMapper.insert(u);
    }
}
