package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.InMemoryDataRepository;
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

import java.util.List;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class StatementControllerTests {

    private final String testStatement = "IVR5cGU6QmFuaw0KRDIwLzA4LzIwMTINClBTVUJXQVkgMjY4NTIgQ0QgWFhYWCANClQtNS4wMA0KXg0KRDE2LzA4LzIwMTINClBPMiBVSyBQQVkgJiBHTyBDRCBYWFhYIA0KVC0xNS4wMA0KXg0KDQoNCkQxNy8wOC8yMDEyDQpQVEVTQ08gU1RPUkUgMjgwOCBDRCBYWFhYIA0KVC0zNi45OQ0KXg0KDQpEMTUvMDgvMjAxMg0KUEJQIEdMRURIT1cgUy9TVE4gQ0QgWFhYWCANClQtNTguOTYNCl4=";

    @Test
    public void emptyGetTest() {
        StatementController controller = this.createController();
        StatementGetDto result = controller.getStatement();

        Assert.assertEquals(0, result.getOperations().size());
    }

    @Test
    public void simpleUpdateTest() {
        StatementController controller = this.createController();
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(testStatement);

        StatementUpdateResponseDto result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());
    }

    @Test
    public void simpleUpdateAndGetTest() {
        StatementController controller = this.createController();
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(testStatement);

        StatementUpdateResponseDto result = controller.updateStatement(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        StatementGetDto getResult = controller.getStatement();

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void simpleUpdateAndGetOrderTest() {
        StatementController controller = this.createController();
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(testStatement);

        controller.updateStatement(request);
        StatementGetDto getResult = controller.getStatement();
        List<OperationDetailsDto> operations = getResult.getOperations();

        Assert.assertEquals(4, operations.size());

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 20), operations.get(0).getDate());
        Assert.assertEquals("SUBWAY 26852 CD XXXX", operations.get(0).getDescription());
        Assert.assertEquals(-5.0, operations.get(0).getAmount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 17), operations.get(1).getDate());
        Assert.assertEquals("TESCO STORE 2808 CD XXXX", operations.get(1).getDescription());
        Assert.assertEquals(-36.99, operations.get(1).getAmount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 16), operations.get(2).getDate());
        Assert.assertEquals("O2 UK PAY & GO CD XXXX", operations.get(2).getDescription());
        Assert.assertEquals(-15.0, operations.get(2).getAmount(), 0.001);

        Assert.assertEquals(TestHelpers.getDate(2012, 8, 15), operations.get(3).getDate());
        Assert.assertEquals("BP GLEDHOW S/STN CD XXXX", operations.get(3).getDescription());
        Assert.assertEquals(-58.96, operations.get(3).getAmount(), 0.001);
    }

    @Test
    public void duplicateUpdateTest() {
        StatementController controller = this.createController();
        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(testStatement);

        StatementUpdateResponseDto result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        result = controller.updateStatement(request);
        Assert.assertEquals(0, result.getInsertedCount());
        Assert.assertEquals(4, result.getDuplicatesCount());

        StatementGetDto getResult = controller.getStatement();

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void multiUserUpdateTest() {
        StatementController controller = this.createController();

        ((MockUserProvider) controller.getUserProvider()).setUsername("ala");

        StatementUpdateDto request = new StatementUpdateDto();
        request.setBase64QifOperations(testStatement);

        StatementUpdateResponseDto result = controller.updateStatement(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        ((MockUserProvider) controller.getUserProvider()).setUsername("kot");

        StatementGetDto getResult = controller.getStatement();

        Assert.assertEquals(0, getResult.getOperations().size());
    }

    private StatementController createController() {
        StatementController controller = new StatementController();

        controller.setOperationsRepository(new InMemoryDataRepository());
        controller.setStatementParser(new QifStatementParser());
        controller.setUserProvider(new MockUserProvider("ala"));
        controller.setCryptoHelper(new DefaultCryptoHelper());

        return controller;
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
