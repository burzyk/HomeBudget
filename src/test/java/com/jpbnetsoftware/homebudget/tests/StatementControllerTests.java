package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import com.jpbnetsoftware.homebudget.domain.impl.QifStatementParser;
import com.jpbnetsoftware.homebudget.service.StatementController;
import com.jpbnetsoftware.homebudget.service.UserProvider;
import com.jpbnetsoftware.homebudget.service.dto.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class StatementControllerTests {

    private final String[] statementParts = new String[]{
            "D20/08/2012\n" +
                    "PSUBWAY 26852 CD XXXX \n" +
                    "T-5.00\n" +
                    "^",
            "D17/08/2012\n" +
                    "PTESCO STORE 2808 CD XXXX \n" +
                    "T-36.99\n" +
                    "^",
            "D16/08/2012\n" +
                    "PO2 UK PAY & GO CD XXXX \n" +
                    "T-15.00\n" +
                    "^",
            "D15/08/2012\n" +
                    "PBP GLEDHOW S/STN CD XXXX \n" +
                    "T-58.96\n" +
                    "^",
            "D20/09/2012\n" +
                    "MC DONNALDS S/STN CD XXXX \n" +
                    "T-33.87\n" +
                    "^",
            "D20/03/2012\n" +
                    "FR S/STN CD XXXX \n" +
                    "T-76.17\n" +
                    "^",
            "D23/09/2013\n" +
                    "BURGER KING S/STN CD XXXX \n" +
                    "T-43.17\n" +
                    "^",
            "D23/09/2013\n" +
                    "PAY 1 S/STN CD XXXX \n" +
                    "T43.17\n" +
                    "^",
            "D15/03/2013\n" +
                    "AZURE S/STN CD XXXX \n" +
                    "T-43.17\n" +
                    "^",
            "D19/03/2013\n" +
                    "PAY 3 S/STN CD XXXX \n" +
                    "T1000.00\n" +
                    "^",
            "D15/08/2015\n" +
                    "AWS S/STN CD XXXX \n" +
                    "T-6.17\n" +
                    "^",
            "D20/08/2015\n" +
                    "SREM S/STN CD XXXX \n" +
                    "T6.17\n" +
                    "^",
            "D21/08/2015\n" +
                    "SEXP S/STN CD XXXX \n" +
                    "T-106.17\n" +
                    "^"
    };

    @Test
    public void emptyGetTest() {
        StatementController controller = this.setupController("ala");
        StatementGetResponse result = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(0, result.getOperations().size());
    }

    @Test
    public void simpleUpdateTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponse result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());
    }

    @Test
    public void simpleUpdateAndGetTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponse result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        StatementGetResponse getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void simpleUpdateAndGetOrderTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        controller.updateStatement(request);
        StatementGetResponse getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);
        List<OperationDetails> operations = getResult.getOperations();

        Assert.assertEquals(4, operations.size());
    }

    @Test
    public void duplicateUpdateTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponse result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        result = controller.updateStatement(request);
        Assert.assertEquals(0, result.getInsertedCount());
        Assert.assertEquals(4, result.getDuplicatesCount());

        StatementGetResponse getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void partialDuplicateUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{0, 1, 2}, 3, 0, new int[]{0, 1, 2});
        validateUpdate(controller, new int[]{1, 2, 3}, 1, 2, new int[]{0, 1, 2, 3});
    }

    @Test
    public void partialDuplicateLeftUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{1, 2}, 2, 0, new int[]{1, 2});
        validateUpdate(controller, new int[]{0, 2}, 1, 1, new int[]{0, 1, 2});
    }

    @Test
    public void partialDuplicateRightUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{1, 2}, 2, 0, new int[]{1, 2});
        validateUpdate(controller, new int[]{2, 3}, 1, 1, new int[]{1, 2, 3});
    }

    @Test
    public void partialDuplicateInsideUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{0, 3}, 2, 0, new int[]{0, 3});
        validateUpdate(controller, new int[]{1, 2}, 2, 0, new int[]{0, 1, 2, 3});
    }

    @Test
    public void partialDuplicateOutsideUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{1, 2}, 2, 0, new int[]{1, 2});
        validateUpdate(controller, new int[]{0, 3}, 2, 0, new int[]{0, 1, 2, 3});
    }

    @Test
    public void partialDuplicateCombUnsortedUpdateTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{2, 0}, 2, 0, new int[]{0, 2});
        validateUpdate(controller, new int[]{3, 1}, 2, 0, new int[]{0, 1, 2, 3});
    }

    @Test
    public void getStatementSequenceTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{2, 0, 1, 3}, 4, 0, new int[]{0, 1, 2, 3});
        validateGetSequence(controller, 1, 2, 2, new int[]{1, 2});
    }

    @Test
    public void getStatementSequenceOutOfBoundsCountTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{2, 0, 1, 3}, 4, 0, new int[]{0, 1, 2, 3});
        validateGetSequence(controller, 3, 20, 1, new int[]{3});
    }

    @Test
    public void getStatementSequenceOutOfBoundsIndexTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{2, 0, 1, 3}, 4, 0, new int[]{0, 1, 2, 3});
        validateGetSequence(controller, 30, 21, 0, new int[]{});
    }

    @Test
    public void getStatementSequenceStreamTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{2, 0, 1, 3}, 4, 0, new int[]{0, 1, 2, 3});
        validateGetSequence(controller, 0, 2, 2, new int[]{0, 1});
        validateGetSequence(controller, 2, 2, 2, new int[]{2, 3});
    }

    @Test
    public void multiUserUpdateTest() {
        StatementController controller = this.setupController("ala", "kot", "madzia");

        ((MockUserProvider) controller.getUserProvider()).setUsername("ala");

        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponse result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        ((MockUserProvider) controller.getUserProvider()).setUsername("kot");
        ((MockUserProvider) controller.getUserProvider()).setPassword("mojetajnehaslokot");

        StatementGetResponse getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(0, getResult.getOperations().size());
    }


    // 20/08/2012  -5.00
    // 17/08/2012  -36.99
    // 16/08/2012  -15.00
    // 15/08/2012  -58.96
    // 20/09/2012  -33.87
    // 20/03/2012  -76.17
    // 23/09/2013  -43.17
    // 23/09/2013  43.17
    // 15/03/2013  -43.17
    // 19/03/2013  1000.00
    // 15/08/2015  -6.17
    // 20/08/2015  6.17
    // 21/08/2015  -106.17


    @Test
    public void getBalancesSimpleTest() {
        StatementController controller = setupController("ala");

        validateUpdate(controller, new int[]{0, 1, 2, 3});
        validateBalances(controller, 8, 2012, 8, 2012, new BalanceDetails[]{
                new BalanceDetails(2012, 8, -115.95, 0)
        });
    }


    private void validateBalances(StatementController controller, int fromMonth, int fromYear, int toMonth, int toYear, BalanceDetails[] expectedBalances) {

        List<BalanceDetails> balances = controller.getBalances(fromMonth, fromYear, toMonth, toYear).getBalances();

        Assert.assertEquals(expectedBalances.length, balances.size());

        for (int i = 0; i < expectedBalances.length; i++) {
            Assert.assertEquals(expectedBalances[i].getTotalSpent(), balances.get(i).getTotalSpent(), 0.001);
            Assert.assertEquals(expectedBalances[i].getTotalEarned(), balances.get(i).getTotalEarned(), 0.001);
            Assert.assertEquals(expectedBalances[i].getMonth(), balances.get(i).getMonth());
            Assert.assertEquals(expectedBalances[i].getYear(), balances.get(i).getYear());
        }

    }

    private void validateGetSequence(StatementController controller, int index, int count, int expectedCount, int[] parts) {
        StatementGetResponse response = controller.getStatementSequence(index, count);
        Assert.assertEquals(expectedCount, response.getOperations().size());

        for (int i = 0; i < parts.length; i++) {
            assertStatement(parts[i], response.getOperations().get(i));
        }
    }

    private void validateUpdate(StatementController controller, int[] newParts) {
        validateUpdate(controller, newParts, newParts.length, 0, newParts);
    }

    private void validateUpdate(StatementController controller, int[] newParts, int inserted, int duplicates, int[] newStatement) {
        StatementUpdateRequest request = new StatementUpdateRequest();
        request.setBase64QifOperations(prepareStatement(newParts));
        StatementUpdateResponse result = controller.updateStatement(request);

        Assert.assertEquals(inserted, result.getInsertedCount());
        Assert.assertEquals(duplicates, result.getDuplicatesCount());

        StatementGetResponse statement = controller.getStatement(LocalDate.MIN, LocalDate.MAX);
        Assert.assertEquals(newStatement.length, statement.getOperations().size());

        for (int i = 0; i < newStatement.length; i++) {
            assertStatement(newStatement[i], statement.getOperations().get(i));
        }
    }

    private StatementController setupController(String username, String... others) {
        String password = "mojetajnehaslo";

        HibernateRepository repo = TestHelpers.createHibernateRepository();
        StatementController controller = new StatementController();

        controller.setOperationsRepository(repo);
        controller.setStatementParser(new QifStatementParser());
        controller.setUserProvider(new MockUserProvider(username, password));
        controller.setCryptoHelper(new DefaultCryptoHelper());

        repo.insertUser(username, password);

        for (String s : others) {
            repo.insertUser(s, password + s);
        }

        return controller;
    }

    private String prepareStatement(int... parts) {
        String statement = "";

        for (int i : parts) {
            statement += statementParts[i] + "\n";
        }

        return new DefaultCryptoHelper().encodeBase64(statement);
    }

    private void assertStatement(int i, OperationDetails operation) {

        switch (i) {
            case 0: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 20), operation.fullDate());
                Assert.assertEquals("SUBWAY 26852 CD XXXX", operation.getDescription());
                Assert.assertEquals(-5.0, operation.getAmount(), 0.001);
                return;
            }
            case 1: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 17), operation.fullDate());
                Assert.assertEquals("TESCO STORE 2808 CD XXXX", operation.getDescription());
                Assert.assertEquals(-36.99, operation.getAmount(), 0.001);
                return;
            }
            case 2: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 16), operation.fullDate());
                Assert.assertEquals("O2 UK PAY & GO CD XXXX", operation.getDescription());
                Assert.assertEquals(-15.0, operation.getAmount(), 0.001);
                return;
            }
            case 3: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 15), operation.fullDate());
                Assert.assertEquals("BP GLEDHOW S/STN CD XXXX", operation.getDescription());
                Assert.assertEquals(-58.96, operation.getAmount(), 0.001);
                return;
            }
            default:
                throw new RuntimeException("Unknown statement");
        }
    }

    class MockUserProvider implements UserProvider {

        private String username;

        private String password;

        public MockUserProvider(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public String getCurrentUsername() {
            return this.getUsername();
        }

        @Override
        public String getCurrentPassword() {
            return this.getPassword();
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
