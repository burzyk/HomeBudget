package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.entities.DbBankOperation;
import com.jpbnetsoftware.homebudget.domain.BankOperation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by pburzynski on 08/05/2016.
 */
public class BasicTests {

    @Test
    public void dbBankOperationCompareTest() {
        DbBankOperation op1 = new DbBankOperation(1, TestHelpers.getDate(2012, 1, 13), "Test", 13.56);
        DbBankOperation op2 = new DbBankOperation(5, TestHelpers.getDate(2012, 1, 13), "Test", 13.56);
        DbBankOperation op3 = new DbBankOperation(1, TestHelpers.getDate(2012, 1, 13), "Test", 13.57);

        Assert.assertEquals(op1, op2);
        Assert.assertNotEquals(op1, op3);
    }

    @Test
    public void dbBankOperationToBankOperationCompareTest() {
        DbBankOperation op1 = new DbBankOperation(1, TestHelpers.getDate(2012, 1, 13), "Test", 13.56);
        BankOperation op2 = new BankOperation(TestHelpers.getDate(2012, 1, 13), "Test", 13.56);

        Assert.assertEquals(op1, op2);
    }
}
