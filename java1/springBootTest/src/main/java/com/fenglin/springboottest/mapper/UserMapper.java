package com.fenglin.springboottest.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenglin.springboottest.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper。
 *
 * <p>继承 MyBatis-Plus 的 {@link BaseMapper} 后，单表增删改查（insert / deleteById /
 * updateById / selectById / selectList / selectCount …）全部开箱即用，
 * 无需再手写 XML 或 SQL。</p>
 *
 * @author fenglin
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
