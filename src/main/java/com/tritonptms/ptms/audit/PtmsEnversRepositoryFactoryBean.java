package com.tritonptms.ptms.audit;

import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.repository.history.RevisionRepository;

public class PtmsEnversRepositoryFactoryBean<
        T extends RevisionRepository<S, ID, N>,
        S,
        ID,
        N extends Number & Comparable<N>>
        extends EnversRevisionRepositoryFactoryBean<T, S, ID, N> {

    public PtmsEnversRepositoryFactoryBean(Class<? extends T> repositoryInterface) {
        super(repositoryInterface);
        setRevisionEntityClass(CustomRevisionEntity.class);
    }
}
