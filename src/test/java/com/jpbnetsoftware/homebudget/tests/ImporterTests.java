package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.domain.BankOperation;
import com.jpbnetsoftware.homebudget.domain.StatementParser;
import com.jpbnetsoftware.homebudget.impl.QifStatementParser;
import org.junit.Assert;
import org.junit.Test;
import scala.collection.immutable.List;

import java.time.LocalDate;

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

    private final String emptyStatement = "!Type:Bank\n";

    private final String testStatementWithBlank = "" +
            "" +
            "" +
            "!Type:Bank\n" +
            "D20/08/2012\n" +
            "PSUBWAY 26852 CD XXXX \n" +
            "T-5.00\n" +
            "" +
            "^\n" +
            "D17/08/2012\n" +
            "PTESCO STORE 2808 CD XXXX \n" +
            "T-36.99\n" +
            "^\n" +
            "D16/08/2012\n" +
            "" +
            "PO2 UK PAY & GO CD XXXX \n" +
            "T-15.00\n" +
            "^\n" +
            "D15/08/2012\n" +
            "" +
            "" +
            "PBP GLEDHOW S/STN CD XXXX \n" +
            "T-58.96\n" +
            "^" +
            "" +
            "";

    private final String unorderedStatement = "!Type:Bank\n" +
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
            "^" +
            "D20/08/2012\n" +
            "PSUBWAY 26852 CD XXXX \n" +
            "T-5.00\n" +
            "^\n";

    @Test
    public void QifStatementParserEmptyStatementTest() {
        StatementParser parser = new QifStatementParser();
        List<BankOperation> operations = parser.parse(emptyStatement).toList();

        Assert.assertEquals(0, operations.size());
    }

    @Test
    public void QifStatementParserSimpleStatementTest() {
        QifStatementParserSimpleTest(testStatement);
    }

    @Test
    public void QifStatementParserSimpleStatementWithBlanksTest() {
        QifStatementParserSimpleTest(testStatementWithBlank);
    }

    @Test
    public void QifStatementParserUnorderedStatementTest() {
        QifStatementParserSimpleTest(unorderedStatement);
    }

    private void QifStatementParserSimpleTest(String statement) {
        StatementParser parser = new QifStatementParser();
        List<BankOperation> operations = parser.parse(statement).toList();

        Assert.assertEquals(4, operations.size());

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 15), operations.apply(0).date());
        Assert.assertEquals("BP GLEDHOW S/STN CD XXXX", operations.apply(0).description());
        Assert.assertEquals(-58.96, operations.apply(0).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 16), operations.apply(1).date());
        Assert.assertEquals("O2 UK PAY & GO CD XXXX", operations.apply(1).description());
        Assert.assertEquals(-15.0, operations.apply(1).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 17), operations.apply(2).date());
        Assert.assertEquals("TESCO STORE 2808 CD XXXX", operations.apply(2).description());
        Assert.assertEquals(-36.99, operations.apply(2).amount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 20), operations.apply(3).date());
        Assert.assertEquals("SUBWAY 26852 CD XXXX", operations.apply(3).description());
        Assert.assertEquals(-5.0, operations.apply(3).amount(), 0.001);
    }


}
