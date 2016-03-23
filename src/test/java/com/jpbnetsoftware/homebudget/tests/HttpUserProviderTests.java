package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import com.jpbnetsoftware.homebudget.service.AuthenticationManager;
import com.jpbnetsoftware.homebudget.service.HttpUserProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class HttpUserProviderTests {

    @Test
    public void noHeaderTest() {
        AuthenticationManager manager = createAuthenticationManager();

        Assert.assertEquals(false, manager.authenticate(null));
        Assert.assertEquals(false, manager.authenticate(""));
        Assert.assertEquals(false, manager.authenticate("  "));
    }

    @Test
    public void correctAuthenticationTest() {
        authenticationTest("kasia", "mojetajnehaslo", true);
    }

    @Test
    public void incorrectUsernameAuthenticationTest() {
        authenticationTest("ala", "mojetajnehaslo", false);
    }

    @Test
    public void incorrectPasswordAuthenticationTest() {
        authenticationTest("kasia", "mojejawnehaslo", false);
    }

    @Test
    public void incorrectDataAuthenticationTest() {
        authenticationTest("ala", "mojejawnehaslo", false);
    }

    private void authenticationTest(String username, String password, boolean isValid) {
        AuthenticationManager manager = createAuthenticationManager();

        int userId = ((HttpUserProvider) manager).getUsersRepository().insertUser(username, password);

        Assert.assertEquals(1, userId);
        Assert.assertEquals(isValid, manager.authenticate("Basic a2FzaWE6bW9qZXRham5laGFzbG8="));
    }

    private AuthenticationManager createAuthenticationManager() {
        HttpUserProvider manager = new HttpUserProvider();
        HibernateRepository repo = new HibernateRepository();
        DefaultCryptoHelper crypto = new DefaultCryptoHelper();

        repo.setCryptoHelper(crypto);

        manager.setCryptoHelper(crypto);
        manager.setUsersRepository(repo);

        return manager;
    }
}
