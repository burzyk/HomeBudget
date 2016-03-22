package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.InMemoryDataRepository;
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
    public void simpleAuthenticationTest() {
        AuthenticationManager manager = createAuthenticationManager();

        int userId = ((HttpUserProvider) manager).getUsersRepository().insertUser("kasia", "mojetajnehaslo");

        Assert.assertEquals(1, userId);
        Assert.assertEquals(true, manager.authenticate("Basic a2FzaWE6bW9qZXRham5laGFzbG8="));
    }

    private AuthenticationManager createAuthenticationManager() {
        HttpUserProvider manager = new HttpUserProvider();
        InMemoryDataRepository repo = new InMemoryDataRepository();
        DefaultCryptoHelper crypto = new DefaultCryptoHelper();

        repo.setCryptoHelper(crypto);

        manager.setCryptoHelper(crypto);
        manager.setUsersRepository(repo);

        return manager;
    }
}
