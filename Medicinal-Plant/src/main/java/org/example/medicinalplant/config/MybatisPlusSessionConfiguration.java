package org.example.medicinalplant.config;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 下 MyBatis-Plus 自动配置可能未注册 {@link SqlSessionFactory}，导致 Mapper 报错：
 * Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required。
 * 在缺少上述 Bean 时，通过注入的 {@link DataSource} 补全（勿在类上使用 ConditionalOnBean(DataSource)，以免早于数据源注册被跳过）。
 */
@Configuration
public class MybatisPlusSessionConfiguration {

    @Bean
    @ConditionalOnMissingBean(SqlSessionFactory.class)
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean.getObject();
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionTemplate.class)
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
