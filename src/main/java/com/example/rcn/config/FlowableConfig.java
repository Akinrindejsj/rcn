package com.example.rcn.config;

import com.example.rcn.flowable.ArticleApprovedDelegate;
import com.example.rcn.flowable.ArticleRejectedDelegate;
import com.example.rcn.flowable.NotifyEditorsDelegate;
import com.example.rcn.flowable.RecordSubmissionDelegate;
import com.example.rcn.service.ArticleService;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowableConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    private final ArticleService articleService;

    public FlowableConfig(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void configure(SpringProcessEngineConfiguration engineConfiguration) {
        engineConfiguration.setDatabaseSchemaUpdate("true");
        engineConfiguration.setAsyncExecutorActivate(true);
    }

    @Bean
    public RecordSubmissionDelegate recordSubmissionDelegate() {
        return new RecordSubmissionDelegate(articleService);
    }

    @Bean
    public NotifyEditorsDelegate notifyEditorsDelegate() {
        return new NotifyEditorsDelegate();
    }

    @Bean
    public ArticleApprovedDelegate articleApprovedDelegate() {
        return new ArticleApprovedDelegate(articleService);
    }

    @Bean
    public ArticleRejectedDelegate articleRejectedDelegate() {
        return new ArticleRejectedDelegate(articleService);
    }
}
