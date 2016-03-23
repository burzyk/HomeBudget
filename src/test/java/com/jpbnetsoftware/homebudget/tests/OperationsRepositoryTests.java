package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.data.OperationsRepository;
import com.jpbnetsoftware.homebudget.data.UsersRepository;
import com.jpbnetsoftware.homebudget.domain.BankOperation;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;

/**
 * Created by pburzynski on 23/03/2016.
 */
public class OperationsRepositoryTests {
    @Test
    public void insertOperationTest() {
        String username = "ala";
        createUsersRepository().insertUser(username, "makota");

        OperationsRepository repo = createOperationsRepository();
        BankOperation operation = new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54);

        repo.insertOperation(username, operation);
        Assert.assertEquals(true, repo.operationExists(username, operation));
    }

    @Test
    public void operationExistsTest() {
        String username = "ala1";
        createUsersRepository().insertUser(username, "makota");

        OperationsRepository repo = createOperationsRepository();
        BankOperation operation = new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54);

        repo.insertOperation(username, operation);
        Assert.assertEquals(true, repo.operationExists(username, operation));
    }

    @Test
    public void operationNotExistsTest() {
        String username = "ala2";
        createUsersRepository().insertUser(username, "makota");

        OperationsRepository repo = createOperationsRepository();

        repo.insertOperation(username, new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54));
        Assert.assertEquals(true, repo.operationExists(username, new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.55)));
    }

    @Test
    public void operationNotExistsWrongUserTest() {
        String username = "ala3";
        createUsersRepository().insertUser(username, "makota");

        OperationsRepository repo = createOperationsRepository();

        repo.insertOperation(username, new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54));
        Assert.assertEquals(true, repo.operationExists(username + "1", new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54)));
    }

    @Test
    public void getOperationsTest() {
        String username = "ala4";
        createUsersRepository().insertUser(username, "makota");

        OperationsRepository repo = createOperationsRepository();

        repo.insertOperation(username, new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        repo.insertOperation(username, new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        repo.insertOperation(username, new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        Map<Object, BankOperation> operations = repo.getOperations(username);

        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.apply(0).date());
        Assert.assertEquals("2", operations.apply(0).description());
        Assert.assertEquals(14.54, operations.apply(0).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.apply(1).date());
        Assert.assertEquals("3", operations.apply(1).description());
        Assert.assertEquals(15.54, operations.apply(1).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.apply(2).date());
        Assert.assertEquals("1", operations.apply(2).description());
        Assert.assertEquals(13.54, operations.apply(2).amount(), 0.001);
    }

    @Test
    public void getOperationsMultitenantTest() {
        String username1 = "ala5";
        String username2 = "ola5";

        createUsersRepository().insertUser(username1, "makota");
        createUsersRepository().insertUser(username2, "maasa");

        OperationsRepository repo = createOperationsRepository();

        repo.insertOperation(username1, new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        repo.insertOperation(username1, new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        repo.insertOperation(username1, new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        repo.insertOperation(username2, new BankOperation(TestHelpers.getDate(2112, 06, 10), "x1", -13.54));
        repo.insertOperation(username2, new BankOperation(TestHelpers.getDate(2107, 06, 10), "x2", -14.54));
        repo.insertOperation(username2, new BankOperation(TestHelpers.getDate(2108, 06, 10), "x3", -15.54));

        Map<Object, BankOperation> operations = repo.getOperations(username1);

        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.apply(0).date());
        Assert.assertEquals("2", operations.apply(0).description());
        Assert.assertEquals(14.54, operations.apply(0).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.apply(1).date());
        Assert.assertEquals("3", operations.apply(1).description());
        Assert.assertEquals(15.54, operations.apply(1).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.apply(2).date());
        Assert.assertEquals("1", operations.apply(2).description());
        Assert.assertEquals(13.54, operations.apply(2).amount(), 0.001);
    }

    private OperationsRepository createOperationsRepository() {
        HibernateRepository repo = new HibernateRepository();

        repo.setCryptoHelper(new DefaultCryptoHelper());

        return repo;
    }

    private UsersRepository createUsersRepository() {
        return (UsersRepository) createOperationsRepository();
    }
}
