package com.tritonptms.ptms;

import com.tritonptms.ptms.audit.PtmsEnversRepositoryFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableEnversRepositories(repositoryFactoryBeanClass = PtmsEnversRepositoryFactoryBean.class)
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class PtmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PtmsApplication.class, args);
    }
}
