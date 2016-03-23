package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.data.UsersRepository;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 23/03/2016.
 */
public class UsersRepositoryTests {
    @Test
    public void createUserTest() {
        UsersRepository repo = createUsersRepository();

        Assert.assertEquals(false, repo.userExists("kasia", "tajnehaslo"));
        repo.insertUser("kasia", "tajnehaslo");
        Assert.assertEquals(true, repo.userExists("kasia", "tajnehaslo"));
    }

    @Test
    public void userExistsTest() {
        UsersRepository repo = createUsersRepository();

        Assert.assertEquals(false, repo.userExists("ala_userExistsTest"));
        repo.insertUser("ala_userExistsTest", "tajnehaslo");
        Assert.assertEquals(true, repo.userExists("ala_userExistsTest"));
    }

    private UsersRepository createUsersRepository() {
        HibernateRepository repo = new HibernateRepository();

        repo.setCryptoHelper(new DefaultCryptoHelper());

        return repo;
    }
}
