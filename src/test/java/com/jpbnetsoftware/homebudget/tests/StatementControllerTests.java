package com.jpbnetsoftware.homebudget.tests;

import com.jpbnetsoftware.homebudget.data.InMemoryOperationsRepository;
import com.jpbnetsoftware.homebudget.impl.QifStatementParser;
import com.jpbnetsoftware.homebudget.service.StatementController;
import com.jpbnetsoftware.homebudget.service.UserIdProvider;
import com.jpbnetsoftware.homebudget.service.dto.OperationDetailsDto;
import com.jpbnetsoftware.homebudget.service.dto.OperationsGetDto;
import com.jpbnetsoftware.homebudget.service.dto.OperationsUpdateDto;
import com.jpbnetsoftware.homebudget.service.dto.OperationsUpdateResponseDto;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by pburzynski on 22/03/2016.
 */
public class StatementControllerTests {

    private final String testStatement = "IVR5cGU6QmFuaw0KRDIwLzA4LzIwMTINClBTVUJXQVkgMjY4NTIgQ0QgWFhYWCANClQtNS4wMA0KXg0KRDE2LzA4LzIwMTINClBPMiBVSyBQQVkgJiBHTyBDRCBYWFhYIA0KVC0xNS4wMA0KXg0KDQoNCkQxNy8wOC8yMDEyDQpQVEVTQ08gU1RPUkUgMjgwOCBDRCBYWFhYIA0KVC0zNi45OQ0KXg0KDQpEMTUvMDgvMjAxMg0KUEJQIEdMRURIT1cgUy9TVE4gQ0QgWFhYWCANClQtNTguOTYNCl4=";

    @Test
    public void EmptyGetTest() {
        StatementController controller = this.createController();
        OperationsGetDto result = controller.getOperations();

        Assert.assertEquals(0, result.getOperations().size());
    }

    @Test
    public void SimpleUpdateTest() {
        StatementController controller = this.createController();
        OperationsUpdateDto request = new OperationsUpdateDto();
        request.setBase64Content(testStatement);

        OperationsUpdateResponseDto result = controller.uploadOperations(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());
    }

    @Test
    public void SimpleUpdateAndGetTest() {
        StatementController controller = this.createController();
        OperationsUpdateDto request = new OperationsUpdateDto();
        request.setBase64Content(testStatement);

        OperationsUpdateResponseDto result = controller.uploadOperations(request);

        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        OperationsGetDto getResult = controller.getOperations();

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void SimpleUpdateAndGetOrderTest() {
        StatementController controller = this.createController();
        OperationsUpdateDto request = new OperationsUpdateDto();
        request.setBase64Content(testStatement);

        controller.uploadOperations(request);
        OperationsGetDto getResult = controller.getOperations();
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
    public void DuplicateUpdateTest() {
        StatementController controller = this.createController();
        OperationsUpdateDto request = new OperationsUpdateDto();
        request.setBase64Content(testStatement);

        OperationsUpdateResponseDto result = controller.uploadOperations(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        result = controller.uploadOperations(request);
        Assert.assertEquals(0, result.getInsertedCount());
        Assert.assertEquals(4, result.getDuplicatesCount());

        OperationsGetDto getResult = controller.getOperations();

        Assert.assertEquals(4, getResult.getOperations().size());
    }

    @Test
    public void MultiUserUpdateTest() {
        StatementController controller = this.createController();

        ((MockUserIdProvider)controller.getUserIdProvider()).setUserId(10);

        OperationsUpdateDto request = new OperationsUpdateDto();
        request.setBase64Content(testStatement);

        OperationsUpdateResponseDto result = controller.uploadOperations(request);
        Assert.assertEquals(4, result.getInsertedCount());
        Assert.assertEquals(0, result.getDuplicatesCount());

        ((MockUserIdProvider)controller.getUserIdProvider()).setUserId(11);

        OperationsGetDto getResult = controller.getOperations();

        Assert.assertEquals(0, getResult.getOperations().size());
    }

    private StatementController createController() {
        StatementController controller = new StatementController();

        controller.setOperationsRepository(new InMemoryOperationsRepository());
        controller.setStatementParser(new QifStatementParser());
        controller.setUserIdProvider(new MockUserIdProvider(13));

        return controller;
    }

    class MockUserIdProvider implements UserIdProvider {

        private int userId;

        public MockUserIdProvider(int userId) {
            this.userId = userId;
        }

        @Override
        public int getCurrentUserId() {
            return this.getUserId();
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
