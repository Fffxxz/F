package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.UserToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录令牌表 Mapper。
 *
 * @author fenglin
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
}
