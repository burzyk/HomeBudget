package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.data.OperationsRepository;
import com.jpbnetsoftware.homebudget.domain.BankOperation;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.Map;

/**
 * Created by pburzynski on 23/03/2016.
 */
public class OperationsRepositoryTests {
    @Test
    public void insertOperationTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        BankOperation operation = new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54);

        repo.insertOperation("ala", operation);
        Assert.assertEquals(true, repo.operationExists("ala", operation));
    }

    @Test
    public void operationExistsTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        BankOperation operation = new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54);

        repo.insertOperation("ala", operation);
        Assert.assertEquals(true, repo.operationExists("ala", operation));
    }

    @Test
    public void operationNotExistsTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54));
        Assert.assertEquals(false, repo.operationExists("ala", new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.55)));
    }

    @Test
    public void operationNotExistsWrongUserTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54));
        Assert.assertEquals(false, repo.operationExists("ola", new BankOperation(TestHelpers.getDate(2012, 06, 10), "x", 13.54)));
    }

    @Test
    public void getOperationsTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        Map<Object, BankOperation> operations = repo.getOperations("ala");

        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.toList().apply(0)._2().date());
        Assert.assertEquals("2", operations.toList().apply(0)._2().description());
        Assert.assertEquals(14.54, operations.toList().apply(0)._2().amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.toList().apply(1)._2().date());
        Assert.assertEquals("3", operations.toList().apply(1)._2().description());
        Assert.assertEquals(15.54, operations.toList().apply(1)._2().amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.toList().apply(2)._2().date());
        Assert.assertEquals("1", operations.toList().apply(2)._2().description());
        Assert.assertEquals(13.54, operations.toList().apply(2)._2().amount(), 0.001);
    }

    @Test
    public void getOperationsMultitenantTest() {
        OperationsRepository repo = setupOperationsRepository("ala", "ola");
        
        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        repo.insertOperation("ala", new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        repo.insertOperation("ola", new BankOperation(TestHelpers.getDate(2112, 06, 10), "x1", -13.54));
        repo.insertOperation("ola", new BankOperation(TestHelpers.getDate(2107, 06, 10), "x2", -14.54));
        repo.insertOperation("ola", new BankOperation(TestHelpers.getDate(2108, 06, 10), "x3", -15.54));

        Map<Object, BankOperation> operations = repo.getOperations("ala");

        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.toList().apply(0)._2().date());
        Assert.assertEquals("2", operations.toList().apply(0)._2().description());
        Assert.assertEquals(14.54, operations.toList().apply(0)._2().amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.toList().apply(1)._2().date());
        Assert.assertEquals("3", operations.toList().apply(1)._2().description());
        Assert.assertEquals(15.54, operations.toList().apply(1)._2().amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.toList().apply(2)._2().date());
        Assert.assertEquals("1", operations.toList().apply(2)._2().description());
        Assert.assertEquals(13.54, operations.toList().apply(2)._2().amount(), 0.001);
    }

    private OperationsRepository setupOperationsRepository(String... usernames) {
        HibernateRepository repo = TestHelpers.createHibernateRepository();

        for (String u : usernames) {
            repo.insertUser(u, u);
        }

        return repo;
    }
}
