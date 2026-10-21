package com.fenglin.springboottest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动类。
 *
 * <p>{@code @MapperScan} 统一扫描 Mapper 接口，Mapper 接口上就只需要写
 * {@code @Mapper} 而无需重复配置。</p>
 */
@SpringBootApplication
@MapperScan("com.fenglin.springboottest.mapper")
public class SpringBootTestApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootTestApplication.class, args);
	}

}
