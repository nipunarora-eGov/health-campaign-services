package org.egov.project.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digit.models.coremodels.mdms.MdmsCriteriaReq;
import org.apache.commons.io.IOUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.http.client.ServiceRequestClient;
import org.egov.common.service.IdGenService;
import org.egov.common.service.MdmsService;
import org.egov.project.config.ProjectConfiguration;
import org.egov.project.helper.BeneficiaryRequestTestBuilder;
import org.egov.project.repository.ProjectBeneficiaryRepository;
import org.egov.project.web.models.BeneficiaryRequest;
import org.egov.project.web.models.Household;
import org.egov.project.web.models.HouseholdResponse;
import org.egov.project.web.models.HouseholdSearchRequest;
import org.egov.project.web.models.Individual;
import org.egov.project.web.models.IndividualResponse;
import org.egov.project.web.models.IndividualSearchRequest;
import org.egov.project.web.models.Project;
import org.egov.project.web.models.ProjectBeneficiary;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectBeneficiaryServiceCreateTest {

    private final String HOUSEHOLD_RESPONSE_FILE_NAME = "/responses/mdms-household-response.json";

    private final String INDIVIDUAL_RESPONSE_FILE_NAME = "/responses/mdms-individual-response.json";

    @InjectMocks
    private ProjectBeneficiaryService projectBeneficiaryService;

    @Mock
    private ProjectService projectService;

    @Mock
    private IdGenService idGenService;

    @Mock
    private MdmsService mdmsService;

    @Mock
    private ServiceRequestClient serviceRequestClient;

    @Mock
    private ProjectBeneficiaryRepository projectBeneficiaryRepository;

    @Mock
    private ProjectConfiguration projectConfiguration;

    private BeneficiaryRequest request;

    @BeforeEach
    void setUp() throws Exception {
        request = BeneficiaryRequestTestBuilder.builder()
                .withOneProjectBeneficiary()
                .build();
        List<String> idList = new ArrayList<>();
        idList.add("some-id");
        lenient().when(idGenService.getIdList(any(RequestInfo.class),
                        any(String.class),
                        eq("project.beneficiary.id"), eq(""), anyInt()))
                .thenReturn(idList);
        ReflectionTestUtils.setField(projectBeneficiaryService, "householdServiceHost", "household-service");
        ReflectionTestUtils.setField(projectBeneficiaryService, "householdServiceSearchUrl", "/v1/_search");
        ReflectionTestUtils.setField(projectBeneficiaryService, "individualServiceHost", "individual-service");
        ReflectionTestUtils.setField(projectBeneficiaryService, "individualServiceSearchUrl", "/v1/_search");
        lenient().when(projectConfiguration.getCreateProjectBeneficiaryTopic()).thenReturn("create-topic");
    }

    private void mockValidateProjectId() {
        lenient().when(projectService.validateProjectIds(
                any(List.class))
            ).thenReturn(Collections.singletonList("some-project-id"));
    }

    private void mockProjectFindIds() {
        when(projectService.findByIds(
                any(List.class)
        )).thenReturn(
                Collections.singletonList(
                        Project.builder().id("some-project-id").projectTypeId("some-project-type-id").build())
        );
    }

    private void mockServiceRequestClientWithHousehold() throws Exception {
        when(serviceRequestClient.fetchResult(
                any(StringBuilder.class),
                any(),
                eq(HouseholdResponse.class))
        ).thenReturn(
                HouseholdResponse.builder().household(Collections.singletonList(Household.builder().build())).build()
        );
    }

    private void mockMdms(String responseFileName) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(responseFileName);
        String responseString = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        ObjectMapper map = new ObjectMapper();
        JsonNode node = map.readTree(responseString);

        when(mdmsService.fetchConfig(any(MdmsCriteriaReq.class), eq(JsonNode.class)))
                .thenReturn(node);
    }

    @Test
    @DisplayName("should enrich the formatted id in project beneficiary")
    void shouldEnrichTheFormattedIdInProjectBeneficiary() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();
        mockServiceRequestClientWithHousehold();

        List<ProjectBeneficiary> projectBeneficiaries = projectBeneficiaryService.create(request);

        assertEquals("some-id", projectBeneficiaries.get(0).getId());
    }

    @Test
    @DisplayName("should send the enriched project beneficiary to the kafka topic")
    void shouldSendTheEnrichedProjectBeneficiaryToTheKafkaTopic() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();
        mockServiceRequestClientWithHousehold();

        projectBeneficiaryService.create(request);

        verify(idGenService, times(1)).getIdList(any(RequestInfo.class),
                any(String.class),
                eq("project.beneficiary.id"), eq(""), anyInt());
        verify(projectBeneficiaryRepository, times(1)).save(any(List.class), any(String.class));
    }

    @Test
    @DisplayName("should update audit details before pushing the project beneficiary to kafka")
    void shouldUpdateAuditDetailsBeforePushingTheProjectBeneficiariesToKafka() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();
        mockServiceRequestClientWithHousehold();

        List<ProjectBeneficiary> projectBeneficiaries = projectBeneficiaryService.create(request);

        assertNotNull(projectBeneficiaries.stream().findAny().get().getAuditDetails().getCreatedBy());
        assertNotNull(projectBeneficiaries.stream().findAny().get().getAuditDetails().getCreatedTime());
        assertNotNull(projectBeneficiaries.stream().findAny().get().getAuditDetails().getLastModifiedBy());
        assertNotNull(projectBeneficiaries.stream().findAny().get().getAuditDetails().getLastModifiedTime());
    }

    @Test
    @DisplayName("should set row version as 1 and deleted as false")
    void shouldSetRowVersionAs1AndDeletedAsFalse() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();
        mockServiceRequestClientWithHousehold();

        List<ProjectBeneficiary> projectBeneficiaries = projectBeneficiaryService.create(request);

        assertEquals(1, projectBeneficiaries.stream().findAny().get().getRowVersion());
        assertFalse(projectBeneficiaries.stream().findAny().get().getIsDeleted());
    }


    @Test
    @DisplayName("should validate correct project id")
    void shouldValidateCorrectProjectId() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();
        mockServiceRequestClientWithHousehold();

        projectBeneficiaryService.create(request);

        verify(projectService, times(1)).validateProjectIds(any(List.class));
    }

    @Test
    @DisplayName("should throw exception for any invalid project id")
    void shouldThrowExceptionForAnyInvalidProjectId() throws Exception {
        when(projectService.validateProjectIds(any(List.class))).thenReturn(Collections.emptyList());

        assertThrows(CustomException.class, () -> projectBeneficiaryService.create(request));
    }

    @Test
    @DisplayName("should throw exception on zero search results")
    void shouldThrowExceptionOnZeroHouseholdSearchResult() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();

        when(serviceRequestClient.fetchResult(any(StringBuilder.class), any(), eq(HouseholdResponse.class)))
                .thenReturn(HouseholdResponse.builder().household(Collections.emptyList()).build());

        assertThrows(CustomException.class, () -> projectBeneficiaryService.create(request));
    }

    @Test
    @DisplayName("should call mdms client and service client for household beneficiary type")
    void shouldCallMdmsClientAndServiceClientWithHouseholdBeneficiaryType() throws Exception {
        mockValidateProjectId();
        mockMdms(HOUSEHOLD_RESPONSE_FILE_NAME);
        mockProjectFindIds();

        when(serviceRequestClient.fetchResult(
                any(StringBuilder.class),
                any(HouseholdSearchRequest.class),
                eq(HouseholdResponse.class))
        ).thenReturn(
                HouseholdResponse.builder().household(Collections.singletonList(Household.builder().build())).build()
        );

        projectBeneficiaryService.create(request);

        verify(projectService, times(1)).validateProjectIds(any(List.class));
        verify(mdmsService, times(1)).fetchConfig(any(), any());
        verify(serviceRequestClient, times(1)).fetchResult(
                any(StringBuilder.class),
                any(HouseholdSearchRequest.class),
                eq(HouseholdResponse.class)
        );
    }

    @Test
    @DisplayName("should call mdms client and service client for individual beneficiary type")
    void shouldCallMdmsClientAndServiceClientWithIndividualBeneficiaryType() throws Exception {
        mockValidateProjectId();
        mockMdms(INDIVIDUAL_RESPONSE_FILE_NAME);
        mockProjectFindIds();

        when(serviceRequestClient.fetchResult(
                any(StringBuilder.class),
                any(IndividualSearchRequest.class),
                eq(IndividualResponse.class))
        ).thenReturn(
                IndividualResponse.builder().individual(Collections.singletonList(Individual.builder().build())).build()
        );

        projectBeneficiaryService.create(request);

        verify(projectService, times(1)).validateProjectIds(any(List.class));
        verify(mdmsService, times(1)).fetchConfig(any(), any());
        verify(serviceRequestClient, times(1)).fetchResult(
                any(StringBuilder.class),
                any(IndividualSearchRequest.class),
                eq(IndividualResponse.class)
        );
    }

    @Test
    @DisplayName("should throw exception on zero search results")
    void shouldThrowExceptionOnZeroIndividualSearchResult() throws Exception {
        mockValidateProjectId();
        mockMdms(INDIVIDUAL_RESPONSE_FILE_NAME);
        mockProjectFindIds();

        when(serviceRequestClient.fetchResult(
                any(StringBuilder.class),
                any(),
                eq(IndividualResponse.class))
        ).thenReturn(
                IndividualResponse.builder().individual(Collections.emptyList()).build()
        );

        assertThrows(CustomException.class, () -> projectBeneficiaryService.create(request));
    }
}