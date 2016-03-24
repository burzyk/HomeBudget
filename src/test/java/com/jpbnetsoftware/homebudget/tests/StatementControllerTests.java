package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.HibernateRepository;
import com.jpbnetsoftware.homebudget.domain.BankOperation;
import com.jpbnetsoftware.homebudget.domain.impl.DefaultCryptoHelper;
import com.jpbnetsoftware.homebudget.domain.impl.QifStatementParser;
import com.jpbnetsoftware.homebudget.service.StatementController;
import com.jpbnetsoftware.homebudget.service.UserProvider;
import com.jpbnetsoftware.homebudget.service.dto.OperationDetailsDto;
import com.jpbnetsoftware.homebudget.service.dto.StatementGetDto;
import com.jpbnetsoftware.homebudget.service.dto.StatementUpdateDto;
import com.jpbnetsoftware.homebudget.service.dto.StatementUpdateResponseDto;
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
                    "^"
    };

    @Test
    public void emptyGetTest() {
        StatementController controller = this.setupController("ala");
        StatementGetDto result = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(0, result.getOperations().size());
    }

    @Test
    public void simpleUpdateTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponseDto result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());
    }

    @Test
    public void simpleUpdateAndGetTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponseDto result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        StatementGetDto getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void simpleUpdateAndGetOrderTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        controller.updateStatement(request);
        StatementGetDto getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);
        List<OperationDetailsDto> operations = getResult.getOperations();

        Assert.assertEquals(4, operations.size());
    }

    @Test
    public void duplicateUpdateTest() {
        StatementController controller = this.setupController("ala");
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponseDto result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        result = controller.updateStatement(request);
        Assert.assertEquals(0, result.getInsertedCount());
        Assert.assertEquals(4, result.getDuplicatesCount());

        StatementGetDto getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

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
    public void multiUserUpdateTest() {
        StatementController controller = this.setupController("ala", "kot", "madzia");

        ((MockUserProvider) controller.getUserProvider()).setUsername("ala");

        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(0, 1, 2, 3));

        StatementUpdateResponseDto result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        ((MockUserProvider) controller.getUserProvider()).setUsername("kot");

        StatementGetDto getResult = controller.getStatement(LocalDate.MIN, LocalDate.MAX);

        Assert.assertEquals(0, getResult.getOperations().size());
    }

    private void validateUpdate(StatementController controller, int[] newParts, int inserted, int duplicates, int[] newStatement) {
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(prepareStatement(newParts));
        StatementUpdateResponseDto result = controller.updateStatement(request);

        Assert.assertEquals(inserted, result.getInsertedCount());
        Assert.assertEquals(duplicates, result.getDuplicatesCount());

        StatementGetDto statement = controller.getStatement(LocalDate.MIN, LocalDate.MAX);
        Assert.assertEquals(newStatement.length, statement.getOperations().size());

        for (int i = 0; i < newStatement.length; i++) {
            assertStatement(newStatement[i], statement.getOperations().get(i));
        }
    }

    private StatementController setupController(String username, String... others) {
        HibernateRepository repo = TestHelpers.createHibernateRepository();
        StatementController controller = new StatementController();

        controller.setOperationsRepository(repo);
        controller.setStatementParser(new QifStatementParser());
        controller.setUserProvider(new MockUserProvider(username));
        controller.setCryptoHelper(new DefaultCryptoHelper());

        repo.insertUser(username, "mojetajnehaslo");

        for (String s : others) {
            repo.insertUser(s, "mojetajnehaslo" + s);
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

    private void assertStatement(int i, OperationDetailsDto operation) {

        switch (i) {
            case 0: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 20), operation.getDate());
                Assert.assertEquals("SUBWAY 26852 CD XXXX", operation.getDescription());
                Assert.assertEquals(-5.0, operation.getAmount(), 0.001);
                return;
            }
            case 1: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 17), operation.getDate());
                Assert.assertEquals("TESCO STORE 2808 CD XXXX", operation.getDescription());
                Assert.assertEquals(-36.99, operation.getAmount(), 0.001);
                return;
            }
            case 2: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 16), operation.getDate());
                Assert.assertEquals("O2 UK PAY & GO CD XXXX", operation.getDescription());
                Assert.assertEquals(-15.0, operation.getAmount(), 0.001);
                return;
            }
            case 3: {
                Assert.assertEquals(TestHelpers.getDate(2012, 8, 15), operation.getDate());
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

        public MockUserProvider(String username) {
            this.username = username;
        }

        @Override
        public String getCurrentUsername() {
            return this.getUsername();
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
