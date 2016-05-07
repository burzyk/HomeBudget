package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.data.OperationsRepository;
import com.jpbnetsoftware.homebudget.data.entities.DbBankOperation;
import com.jpbnetsoftware.homebudget.domain.BankOperation;
import com.jpbnetsoftware.homebudget.domain.InvalidKeyException;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.LinearSeq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Seq;
import scala.collection.mutable.HashSet;
import scala.collection.mutable.LinkedList;

import java.time.LocalDate;

/**
 * Created by pburzynski on 23/03/2016.
 */
public class OperationsRepositoryTests {

    @Test
    public void getOperationsTest() {
        OperationsRepository repo = setupOperationsRepository("ala");

        HashSet<BankOperation> operationsSet = new HashSet<BankOperation>();
        operationsSet.$plus$eq(new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        operationsSet.$plus$eq(new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        operationsSet.$plus$eq(new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        repo.insertOperations("ala", "ala", operationsSet.toSeq());

        List<DbBankOperation> operations = repo.getOperations("ala", "ala", LocalDate.of(2000, 01, 01), LocalDate.of(2500, 01, 01));

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.toList().apply(0).date());
        Assert.assertEquals("1", operations.toList().apply(0).description());
        Assert.assertEquals(13.54, operations.toList().apply(0).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.toList().apply(1).date());
        Assert.assertEquals("3", operations.toList().apply(1).description());
        Assert.assertEquals(15.54, operations.toList().apply(1).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.toList().apply(2).date());
        Assert.assertEquals("2", operations.toList().apply(2).description());
        Assert.assertEquals(14.54, operations.toList().apply(2).amount(), 0.001);
    }

    @Test
    public void getOperationsMultitenantTest() {
        OperationsRepository repo = setupOperationsRepository("ala", "ola");

        HashSet<BankOperation> alaOperations = new HashSet<BankOperation>();
        alaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        alaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        alaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        HashSet<BankOperation> olaOperations = new HashSet<BankOperation>();
        olaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));
        olaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2007, 06, 10), "2", 14.54));
        olaOperations.$plus$eq(new BankOperation(TestHelpers.getDate(2008, 06, 10), "3", 15.54));

        repo.insertOperations("ala", "ala", alaOperations.toSeq());
        repo.insertOperations("ola", "ola", olaOperations.toSeq());

        List<DbBankOperation> operations = repo.getOperations("ala", "ala", LocalDate.of(2000, 01, 01), LocalDate.of(2500, 01, 01));

        Assert.assertEquals(TestHelpers.getDate(2012, 06, 10), operations.toList().apply(0).date());
        Assert.assertEquals("1", operations.toList().apply(0).description());
        Assert.assertEquals(13.54, operations.toList().apply(0).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2008, 06, 10), operations.toList().apply(1).date());
        Assert.assertEquals("3", operations.toList().apply(1).description());
        Assert.assertEquals(15.54, operations.toList().apply(1).amount(), 0.001);


        Assert.assertEquals(TestHelpers.getDate(2007, 06, 10), operations.toList().apply(2).date());
        Assert.assertEquals("2", operations.toList().apply(2).description());
        Assert.assertEquals(14.54, operations.toList().apply(2).amount(), 0.001);
    }

    @Test(expected = InvalidKeyException.class)
    public void invalidPasswordTest() {
        OperationsRepository repo = setupOperationsRepository("ala");
        HashSet<BankOperation> operations = new HashSet<BankOperation>();
        operations.$plus$eq(new BankOperation(TestHelpers.getDate(2012, 06, 10), "1", 13.54));

        repo.insertOperations("ala", "valid-password", operations.toSeq());
        repo.getOperations("ala", "invalid-password", LocalDate.of(2000, 01, 01), LocalDate.of(2500, 01, 01));
    }

    private OperationsRepository setupOperationsRepository(String... usernames) {
        HibernateRepository repo = TestHelpers.createHibernateRepository();

        for (String u : usernames) {
            repo.insertUser(u, u);
        }

        return repo;
    }
}
