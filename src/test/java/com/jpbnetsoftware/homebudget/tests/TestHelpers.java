package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.EntityManagerProvider;
import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;

/**
 * Created by pburzynski on 22/03/2016.
 */
public final class TestHelpers {
    public static LocalDate getDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    public static HibernateRepository createHibernateRepository() {
        HibernateRepository repo = new AlwaysNewSessionFactoryHibernateRepository();
        repo.setCryptoHelper(new DefaultCryptoHelper());

        return repo;
    }

    // This class recreates a new session factory each time a new instance is created
    // It's used in conjunction with 'create' option of hibernate to create a pristine DB
    // for every test case
    private static class AlwaysNewSessionFactoryHibernateRepository extends HibernateRepository {

        private EntityManagerFactory instance;

        @Override
        public EntityManagerFactory entityManagerFactory() {
            return instance = instance == null
                    ? EntityManagerProvider.createEntityManagerFactory()
                    : instance;
        }
    }
}
