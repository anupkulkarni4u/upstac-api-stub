package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.LabResult;
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
class ConsultationControllerTest {


    @Autowired
    ConsultationController consultationController;


    @Autowired
    TestRequestQueryService testRequestQueryService;
    
    @Autowired
    TestRequestRepository testRequestRepository;


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status(){
        
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);
    
        //Act
        TestRequest result =  consultationController.assignForConsultation(testRequest.getRequestId());
        
        //Assert
        assertNotNull(result.getConsultation());
        assertThat(result.getRequestId(),equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(),equalTo(RequestStatus.DIAGNOSIS_IN_PROCESS));

        //Implement this method
        //Create another object of the TestRequest method and explicitly assign this object for Consultation using assignForConsultation() method
        // from consultationController class. Pass the request id of testRequest object.
        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'
        // make use of assertNotNull() method to make sure that the consultation value of second object is not null
        // use getConsultation() method to get the lab result
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        testRequestRepository.save(updateSampleTestRequestInDBForTesting(status));
        return testRequestQueryService.findBy(status).get(0);
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception(){

        //Arrange
        Long invalidRequestId= -34L;
    
        //Act
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            consultationController.assignForConsultation(invalidRequestId);
        });
    
        //Assert
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));

        //Implement this method
        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForConsultation() method
        // of consultationController with InvalidRequestId as Id
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details(){

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
        
        //Act
        TestRequest result =  consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);
    
        //Assert
        assertThat(result.getRequestId(),equalTo(testRequest.getRequestId()));
        assertThat(result.getStatus(),equalTo(RequestStatus.COMPLETED));
        assertThat(result.getConsultation().getSuggestion(),equalTo(createConsultationRequest.getSuggestion()));

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'COMPLETED'. Make use of updateConsultation() method from consultationController class
        // (Pass the previously created two objects as parameters)
        // (for the object of TestRequest class, pass its ID using getRequestId())
        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'COMPLETED'
        // 3. the suggestion of both the objects created should be same. Make use of getSuggestion() method to get the results.
    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception(){

        //Arrange
        Long invalidRequestId= -34L;
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
    
        //Act
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(invalidRequestId,createConsultationRequest);
        });
    
        //Assert
        assertThat(responseStatusException.getMessage(), containsString("Invalid ID"));

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception(){

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest();
        createConsultationRequest.setSuggestion(null);
    
        //Act
        ResponseStatusException responseStatusException = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);
        });
    
        //Arrange
        assertThat(responseStatusException.getMessage(), containsString("ConstraintViolationException"));

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Set the suggestion of the above created object to null.
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
    }

    public CreateConsultationRequest getCreateConsultationRequest() {
        CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
        createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
        createConsultationRequest.setComments("Ok");
        return createConsultationRequest;
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
        testRequest.setLabResult(createSampleLabResult());
        return testRequest;
    }

    private User createSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUserName("someuser");
        return user;
    }

    private LabResult createSampleLabResult() {
        LabResult labResult = new LabResult();
        labResult.setResultId(2L);
        labResult.setResult(TestStatus.NEGATIVE);
        return  null;
    }

}