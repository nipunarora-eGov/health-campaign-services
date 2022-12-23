package org.egov.product.util;

import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.helper.AuditDetailsTestBuilder;
import org.egov.common.helper.RequestInfoTestBuilder;
import org.egov.product.web.models.ApiOperation;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("should check if the request is for update")
    void shouldCheckIfTheRequestIsForUpdate() {
        SomeRequest someRequest = SomeRequest.builder().apiOperation(ApiOperation.UPDATE).build();

        assertTrue(CommonUtils.isForUpdate(someRequest));
    }

    @Test
    @DisplayName("should handle for null api operation")
    void shouldHandleForNullForNullApiOperation() {
        SomeRequest someRequest = SomeRequest.builder().apiOperation(null).build();

        assertFalse(CommonUtils.isForUpdate(someRequest));
    }

    @Test
    @DisplayName("should throw custom exception if invalid object is passed for update")
    void shouldThrowCustomExceptionIfInvalidObjectIsPassedForUpdate() {
        List<String> list = new ArrayList<>();

        assertThrows(CustomException.class, () -> CommonUtils.isForUpdate(list));
    }

    @Test
    @DisplayName("should check if the request is for Delete")
    void shouldCheckIfTheRequestIsForDelete() {
        SomeRequest someRequest = SomeRequest.builder().apiOperation(ApiOperation.DELETE).build();

        assertTrue(CommonUtils.isForDelete(someRequest));
    }

    @Test
    @DisplayName("should check if the request is for create")
    void shouldCheckIfTheRequestIsForCreate() {
        SomeRequest someRequest = SomeRequest.builder().apiOperation(ApiOperation.CREATE).build();

        assertTrue(CommonUtils.isForCreate(someRequest));
    }

    @Test
    @DisplayName("should check if the request is for create when api operation is null")
    void shouldCheckIfTheRequestIsForCreateWhenApiOperationIsNull() {
        SomeRequest someRequest = SomeRequest.builder().apiOperation(null).build();

        assertTrue(CommonUtils.isForCreate(someRequest));
    }

    @Test
    @DisplayName("should create a set of given attribute")
    void shouldCreateASetOfGivenAttribute() {
        SomeObject someObject = SomeObject.builder().id("some-id-1").build();
        SomeObject otherSomeObject = SomeObject.builder().id("some-id-2").build();
        List<SomeObject> objects = new ArrayList<>();
        objects.add(someObject);
        objects.add(otherSomeObject);

        assertEquals(2, CommonUtils.getSet(objects, "getId").size());
    }

    @Test
    @DisplayName("should get the difference of lists when one list is sublist of another")
    void shouldGetTheDifferenceOfListsWhenOneListIsSublistOfAnother() {
        List<String> idList = new ArrayList<>();
        idList.add("some-id");
        idList.add("bad-id");
        List<String> otherIdList = new ArrayList<>();
        otherIdList.add("some-id");

        assertEquals(1, CommonUtils.getDifference(idList, otherIdList).size());
    }

    @Test
    @DisplayName("should get the difference of lists when both the lists have same number of items")
    void shouldGetTheDifferenceOfListsWhenBothTheListsHaveSameNumberOfItems() {
        List<String> idList = new ArrayList<>();
        idList.add("some-id");
        idList.add("other-id");
        List<String> otherIdList = new ArrayList<>();
        otherIdList.add("some-id");
        otherIdList.add("other-id");

        assertEquals(0, CommonUtils.getDifference(idList, otherIdList).size());
    }

    @Test
    @DisplayName("should validate the ids as per the given validator")
    void shouldValidateTheIdsAsPerTheGivenValidator() {
        Set<String> idSet = new HashSet<>();
        idSet.add("some-id");
        idSet.add("other-id");
        UnaryOperator<List<String>> validator = UnaryOperator.identity();

        assertDoesNotThrow(() -> CommonUtils.validateIds(idSet, validator));
    }

    @Test
    @DisplayName("should throw exception in case an invalid id is found")
    void shouldThrowExceptionInCaseAnInvalidIdIsFound() {
        Set<String> idSet = new HashSet<>();
        idSet.add("some-id");
        idSet.add("other-id");
        UnaryOperator<List<String>> validator = (idList) -> {
            idList.remove(0);
            return idList;
        };

        assertDoesNotThrow(() -> CommonUtils.validateIds(idSet, validator));
    }

    @Test
    @DisplayName("should get audit details for create")
    void shouldGetAuditDetailsForCreate() {
        RequestInfo requestInfo = RequestInfoTestBuilder.builder()
                .withCompleteRequestInfo().build();

        AuditDetails auditDetails = CommonUtils.getAuditDetailsForCreate(requestInfo);

        assertEquals(auditDetails.getCreatedTime(), auditDetails.getLastModifiedTime());
        assertEquals(auditDetails.getCreatedBy(), auditDetails.getLastModifiedBy());
        assertTrue(auditDetails.getCreatedTime() != null
                && auditDetails.getLastModifiedTime() != null
                && auditDetails.getCreatedBy() != null
                && auditDetails.getLastModifiedBy() != null);
    }

    @Test
    @DisplayName("should get audit details for update")
    void shouldGetAuditDetailsForUpdate() {
        RequestInfo requestInfo = RequestInfoTestBuilder.builder()
                .withCompleteRequestInfo().build();
        AuditDetails existingAuditDetails = CommonUtils.getAuditDetailsForCreate(requestInfo);

        requestInfo.getUserInfo().setUuid("other-uuid");
        AuditDetails auditDetails = CommonUtils.getAuditDetailsForUpdate(existingAuditDetails,
                requestInfo.getUserInfo().getUuid());

        assertEquals(auditDetails.getCreatedTime(), existingAuditDetails.getCreatedTime());
        assertEquals(auditDetails.getCreatedBy(), existingAuditDetails.getCreatedBy());
        assertNotEquals(auditDetails.getLastModifiedBy(), existingAuditDetails.getLastModifiedBy());
        assertTrue(auditDetails.getCreatedTime() != null
                && auditDetails.getLastModifiedTime() != null
                && auditDetails.getCreatedBy() != null
                && auditDetails.getLastModifiedBy() != null);
    }

    @Test
    @DisplayName("should return true if search is by id only")
    void shouldReturnTrueIfSearchIsByIdOnly() {
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .build();

        assertTrue(CommonUtils.isSearchByIdOnly(someObject));
    }

    @Test
    @DisplayName("should return false if search is not by id only")
    void shouldReturnFalseIfSearchIsNotByIdOnly() {
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .otherField("other-field")
                .build();

        assertFalse(CommonUtils.isSearchByIdOnly(someObject));
    }

    @Test
    @DisplayName("should check row version")
    void shouldCheckRowVersion() {
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(1)
                .build();
        Map<String, SomeObject> idToObjMap = new HashMap<>();
        idToObjMap.put(someObject.getId(), someObject);
        SomeObject otherObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(1)
                .build();
        List<SomeObject> objList = new ArrayList<>();
        objList.add(otherObject);

        assertDoesNotThrow(() -> CommonUtils.checkRowVersion(idToObjMap, objList));
    }

    @Test
    @DisplayName("should throw exception if row versions mismatch")
    void shouldThrowExceptionIfRowVersionsMismatch() {
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(1)
                .build();
        Map<String, SomeObject> idToObjMap = new HashMap<>();
        idToObjMap.put(someObject.getId(), someObject);
        SomeObject otherObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .build();
        List<SomeObject> objList = new ArrayList<>();
        objList.add(otherObject);

        assertThrows(CustomException.class, () -> CommonUtils.checkRowVersion(idToObjMap, objList));
    }

    @Test
    @DisplayName("should fetch tenantId")
    void shouldFetchTenantId() {
        List<SomeObject> objList = new ArrayList<>();
        SomeObject someObject = SomeObject.builder()
                .tenantId("some-tenant-id")
                .build();
        objList.add(someObject);

        assertEquals("some-tenant-id", CommonUtils.getTenantId(objList));
    }

    @Test
    @DisplayName("should enrich with audit details and id")
    void shouldEnrichWithAuditDetailsAndId() {
        RequestInfo requestInfo = RequestInfoTestBuilder.builder()
                .withCompleteRequestInfo().build();
        List<String> idList = new ArrayList<>();
        idList.add("some-id");
        SomeObject someObject = SomeObject.builder().otherField("other-field").build();
        List<SomeObject> objList = new ArrayList<>();
        objList.add(someObject);

        CommonUtils.enrichForCreate(objList, idList, requestInfo);

        assertNotNull(objList.stream().findAny().get().getAuditDetails());
    }

    @Test
    @DisplayName("should create id to obj map")
    void shouldCreateIdToObjMap() {
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .build();
        List<SomeObject> objList = new ArrayList<>();
        objList.add(someObject);

        assertEquals(someObject, CommonUtils.getIdToObjMap(objList).get("some-id"));
    }

    @Test
    @DisplayName("should validate entity")
    void shouldValidateEntity() {
        Map<String, SomeObject> idToObjInRequestMap = new HashMap<>();
        List<SomeObject> objInDbList = new ArrayList<>();
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .build();
        idToObjInRequestMap.put("some-id", someObject);
        objInDbList.add(someObject);

        assertDoesNotThrow(() -> CommonUtils.validateEntities(idToObjInRequestMap, objInDbList));
    }

    @Test
    @DisplayName("should throw exception if there are invalid entities")
    void shouldThrowExceptionIfThereAreInvalidEntities() {
        Map<String, SomeObject> idToObjInRequestMap = new HashMap<>();
        List<SomeObject> objInDbList = new ArrayList<>();
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .build();
        idToObjInRequestMap.put("some-id", someObject);

        assertThrows(CustomException.class,
                () -> CommonUtils.validateEntities(idToObjInRequestMap, objInDbList));
    }

    @Test
    @DisplayName("should enrich for update")
    void shouldEnrichForUpdate() {
        Map<String, SomeObject> idToObjInRequestMap = new HashMap<>();
        List<SomeObject> objInDbList = new ArrayList<>();
        SomeObject someObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .build();
        idToObjInRequestMap.put("some-id", someObject);
        SomeObject otherObject = SomeObject.builder()
                .id("some-id")
                .rowVersion(2)
                .auditDetails(AuditDetailsTestBuilder.builder()
                        .withAuditDetails()
                        .build())
                .build();
        objInDbList.add(otherObject);
        SomeRequest someRequest = SomeRequest.builder().apiOperation(ApiOperation.DELETE)
                .requestInfo(RequestInfoTestBuilder.builder()
                        .withCompleteRequestInfo()
                .build())
                .build();

        CommonUtils.enrichForUpdate(idToObjInRequestMap, objInDbList, someRequest);

        assertEquals(idToObjInRequestMap.get("some-id").getRowVersion(),
                objInDbList.get(0).getRowVersion() + 1);
        assertTrue(idToObjInRequestMap.get("some-id").getIsDeleted());
    }

    @Data
    @Builder
    public static class SomeRequest {
        private ApiOperation apiOperation;
        private RequestInfo requestInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SomeObject {
        private String id;
        private String otherField;
        private Integer rowVersion;
        private String tenantId;
        private Boolean isDeleted;
        private AuditDetails auditDetails;
    }
}