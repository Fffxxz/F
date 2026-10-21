package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.EmailCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮箱验证码表 Mapper。
 *
 * @author fenglin
 */
@Mapper
public interface EmailCodeMapper extends BaseMapper<EmailCode> {
}
