package com.tritonptms.ptms;

import com.tritonptms.ptms.infrastructure.audit.PtmsEnversRepositoryFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import java.time.Clock;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableEnversRepositories(repositoryFactoryBeanClass = PtmsEnversRepositoryFactoryBean.class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PtmsApplication {

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of("Asia/Colombo"));
    }

    public static void main(String[] args) {
        SpringApplication.run(PtmsApplication.class, args);
    }
}
