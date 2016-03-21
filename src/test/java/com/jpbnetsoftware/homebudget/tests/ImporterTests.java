package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.domain.BankOperation;
import com.jpbnetsoftware.homebudget.domain.StatementParser;
import com.jpbnetsoftware.homebudget.impl.QifStatementParser;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.List;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by pburzynski on 21/03/2016.
 */

public class ImporterTests {

    private final String testStatement = "!Type:Bank\n" +
            "D20/08/2012\n" +
            "PSUBWAY 26852 CD XXXX \n" +
            "T-5.00\n" +
            "^\n" +
            "D17/08/2012\n" +
            "PTESCO STORE 2808 CD XXXX \n" +
            "T-36.99\n" +
            "^\n" +
            "D16/08/2012\n" +
            "PO2 UK PAY & GO CD XXXX \n" +
            "T-15.00\n" +
            "^\n" +
            "D15/08/2012\n" +
            "PBP GLEDHOW S/STN CD XXXX \n" +
            "T-58.96\n" +
            "^";

    @Test
    public void QifStatementParserSimpleTest() {
        StatementParser parser = new QifStatementParser();
        List<BankOperation> operations = parser.parse(testStatement).toList();

        Assert.assertEquals(4, operations.size());

        Assert.assertEquals(getDate(2012, 8, 15), operations.apply(0).getDate());
        Assert.assertEquals("BP GLEDHOW S/STN CD XXXX", operations.apply(0).getDescription());
        Assert.assertEquals(-58.96, operations.apply(0).getAmount(), 0.001);

        Assert.assertEquals(getDate(2012, 8, 16), operations.apply(1).getDate());
        Assert.assertEquals("O2 UK PAY & GO CD XXXX", operations.apply(1).getDescription());
        Assert.assertEquals(-15.0, operations.apply(1).getAmount(), 0.001);

        Assert.assertEquals(getDate(2012, 8, 17), operations.apply(2).getDate());
        Assert.assertEquals("TESCO STORE 2808 CD XXXX", operations.apply(2).getDescription());
        Assert.assertEquals(-36.99, operations.apply(2).getAmount(), 0.001);

        Assert.assertEquals(getDate(2012, 8, 20), operations.apply(3).getDate());
        Assert.assertEquals("SUBWAY 26852 CD XXXX", operations.apply(3).getDescription());
        Assert.assertEquals(-5.0, operations.apply(3).getAmount(), 0.001);
    }

    private Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        return calendar.getTime();
    }
}
