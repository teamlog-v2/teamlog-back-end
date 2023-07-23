package com.test.teamlog.global.config;

import com.p6spy.engine.spy.P6SpyOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.annotation.PostConstruct;

// https://backtony.github.io/spring/2021-08-13-spring-log-1/

@Configuration
@DependsOn("prettyLineFormat")
public class P6spyConfig {
    @PostConstruct
    public void setLogMessageFormat() {
        P6SpyOptions.getActiveInstance().setLogMessageFormat(PrettyLineFormat.class.getName());
    }
}
