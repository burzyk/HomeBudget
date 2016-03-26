package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.service.AccountController;
import com.jpbnetsoftware.homebudget.service.dto.AccountAuthenticateRequest;
import com.jpbnetsoftware.homebudget.service.dto.AccountAuthenticateResponse;
import com.jpbnetsoftware.homebudget.service.dto.AccountRegisterRequest;
import com.jpbnetsoftware.homebudget.service.dto.AccountRegisterResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 26/03/2016.
 */
public class AccountControllerTests {

    @Test
    public void registerTest() {
        registerUser(setupController(), "kasia", "mojetajnehaslo", true);
    }

    @Test
    public void duplicateUserRegisterTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "kasia", "mojetajnehaslo", false, true);
    }

    @Test
    public void duplicateUserDifferentPasswordTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "kasia", "innehaslo", false);
    }

    @Test
    public void loginInvalidUsernameTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "ola", "mojetajnehaslo", true);
        loginUser(controller, "asia", "mojetajnehaslo", false);
    }

    @Test
    public void loginInvalidPasswordTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "ola", "mojetajnehaslo", true);
        loginUser(controller, "kasia", "xxx", false);
    }

    @Test
    public void loginValidCredentialsTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "ola", "mojetajnehaslo", true);
        registerUser(controller, "asia", "xxx", true);
        loginUser(controller, "kasia", "mojetajnehaslo", true);
        loginUser(controller, "ola", "mojetajnehaslo", true);
        loginUser(controller, "asia", "xxx", true);
    }

    @Test
    public void loginNotExistentUserTest() {
        AccountController controller = setupController();
        registerUser(controller, "kasia", "mojetajnehaslo", true);
        registerUser(controller, "ola", "mojetajnehaslo", true);
        loginUser(controller, "asia", "mojetajnehaslo", false);
    }

    private void loginUser(AccountController controller, String username, String password, boolean result) {
        AccountAuthenticateRequest request = new AccountAuthenticateRequest();
        request.setUsername(username);
        request.setPassword(password);

        AccountAuthenticateResponse response = controller.authenticate(request);
        Assert.assertEquals(result, response.getAuthenticated());
    }

    private void registerUser(AccountController controller, String username, String password, boolean result) {
        registerUser(controller, username, password, result, result);
    }

    private void registerUser(AccountController controller, String username, String password, boolean result, boolean userExists) {
        AccountRegisterRequest request = new AccountRegisterRequest();
        request.setUsername(username);
        request.setPassword(password);

        AccountRegisterResponse response = controller.register(request);
        Assert.assertEquals(result, response.getRegistered());
        Assert.assertEquals(userExists, controller.getUsersRepository().userExists(request.getUsername(), request.getPassword()));
    }

    private AccountController setupController() {
        AccountController controller = new AccountController();
        controller.setUsersRepository(TestHelpers.createHibernateRepository());

        return controller;
    }
}
