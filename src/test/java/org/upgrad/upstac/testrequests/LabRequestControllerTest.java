package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.time.LocalDate;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Slf4j
class LabRequestControllerTest {
    
    @Autowired
    LabRequestController labRequestController;
    
    @Autowired
    TestRequestQueryService testRequestQueryService;
    
    @Autowired
    TestRequestRepository testRequestRepository;
    
    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status(){

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);
        
        //Act
        TestRequest result =  labRequestController.assignForLabTest(testRequest.getRequestId());
        
        //Assert
        assertNotNull(result.getLabResult());
        assertThat(result.getRequestId(),equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(),equalTo(RequestStatus.LAB_TEST_IN_PROGRESS));
        
        //Implement this method
        //Create another object of the TestRequest method and explicitly assign this object for Lab Test using assignForLabTest() method
        // from labRequestController class. Pass the request id of testRequest object.
        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_IN_PROGRESS'
        // make use of assertNotNull() method to make sure that the lab result of second object is not null
        // use getLabResult() method to get the lab result
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        testRequestRepository.save(updateSampleTestRequestInDBForTesting(status));
        return testRequestQueryService.findBy(status).get(0);
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception(){

        //Arrange
        Long invalidRequestId= -34L;
    
        //Act
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            labRequestController.assignForLabTest(invalidRequestId);
        });
    
        //Assert
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));
       
        //Implement this method
        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForLabTest() method
        // of labRequestController with InvalidRequestId as Id
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details(){

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
        CreateLabResult createLabResult = getCreateLabResult(testRequest);
    
        //Act
        TestRequest result =  labRequestController.updateLabTest(testRequest.getRequestId(),createLabResult);
        
        //Assert
        assertThat(result.getRequestId(),equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(),equalTo(RequestStatus.LAB_TEST_COMPLETED));
        assertThat(result.getLabResult().getResult(),equalTo(createLabResult.getResult()));
        
        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'LAB_TEST_IN_PROGRESS'. Make use of updateLabTest() method from labRequestController class (Pass the previously created two objects as parameters)
        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_COMPLETED'
        // 3. the results of both the objects created should be same. Make use of getLabResult() method to get the results.
    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception(){

        //Arrange
        Long invalidRequestId= -34L;
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        
        //Act
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            labRequestController.updateLabTest(invalidRequestId,createLabResult);
        });
    
        //Assert
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));
        
        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception(){

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        createLabResult.setResult(null);
    
        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()->{
            labRequestController.updateLabTest(testRequest.getRequestId(),createLabResult);
        });
    
        //Assert
        assertThat(exception.getMessage(), containsString("ConstraintViolationException"));

        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Set the result of the above created object to null.
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "ConstraintViolationException"
    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setBloodPressure("120/80");
        createLabResult.setHeartBeat("88");
        createLabResult.setOxygenLevel("95");
        createLabResult.setTemperature("98");
        createLabResult.setComments("Good");
        createLabResult.setResult(TestStatus.NEGATIVE);
        return createLabResult;
        //Create an object of CreateLabResult and set all the values
        // Return the object
        //return null; // Replace this line with your code
    }
    
    public TestRequest updateSampleTestRequestInDBForTesting(RequestStatus status) {
        TestRequest testRequest = new TestRequest();
        testRequest.setRequestId(new Random().nextLong());
        testRequest.setName("someuser");
        testRequest.setCreated(LocalDate.now());
        testRequest.setStatus(status);
        testRequest.setAge(78);
        testRequest.setEmail("someone" + "123456789" + "@somedomain.com");
        testRequest.setPhoneNumber("123456789");
        testRequest.setPinCode(716768);
        testRequest.setAddress("some Addres");
        testRequest.setGender(Gender.MALE);
        testRequest.setCreatedBy(createSampleUser());
        return testRequest;
    }
    
    private User createSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("someuser");
        return user;
    }

}