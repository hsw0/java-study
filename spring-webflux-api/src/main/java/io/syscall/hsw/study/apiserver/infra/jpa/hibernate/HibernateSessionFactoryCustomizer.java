package io.syscall.hsw.study.apiserver.infra.jpa.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.internal.SessionFactoryOptionsBuilder;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateSessionFactoryCustomizer implements Integrator {

    @Override
    public void integrate(
            Metadata metadata, BootstrapContext bootstrapContext, SessionFactoryImplementor sessionFactory) {
        if (sessionFactory.getSessionFactoryOptions() instanceof SessionFactoryOptionsBuilder builder) {
            EntityNotFoundDelegate thrower;
            builder.applyEntityNotFoundDelegate(new DetailedEntityNotFoundDelegate());
        }
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        // nothing to do
    }
}
