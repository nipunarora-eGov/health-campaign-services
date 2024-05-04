package org.egov.transformer.transformationservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.facility.Facility;
import org.egov.common.models.project.Project;
import org.egov.common.models.project.ProjectBeneficiary;
import org.egov.common.models.referralmanagement.Referral;
import org.egov.transformer.config.TransformerProperties;

import org.egov.transformer.models.downstream.ReferralIndexV1;
import org.egov.transformer.producer.Producer;
import org.egov.transformer.service.FacilityService;
import org.egov.transformer.service.IndividualService;
import org.egov.transformer.service.ProjectService;
import org.egov.transformer.service.UserService;
import org.egov.transformer.utils.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.egov.transformer.Constants.*;

@Slf4j
@Component
public class ReferralTransformationService {

    private final TransformerProperties transformerProperties;
    private final Producer producer;
    private final UserService userService;
    private final ProjectService projectService;
    private final IndividualService individualService;
    private final FacilityService facilityService;

    private final CommonUtils commonUtils;

    private final ObjectMapper objectMapper;

    public ReferralTransformationService(TransformerProperties transformerProperties,
                                         Producer producer, UserService userService, ProjectService projectService, IndividualService individualService, FacilityService facilityService, CommonUtils commonUtils, ObjectMapper objectMapper) {
        this.transformerProperties = transformerProperties;
        this.producer = producer;
        this.userService = userService;
        this.projectService = projectService;
        this.individualService = individualService;
        this.facilityService = facilityService;
        this.commonUtils = commonUtils;
        this.objectMapper = objectMapper;
    }

    public void transform(List<Referral> referralList) {
        String topic = transformerProperties.getTransformerProducerReferralIndexV1Topic();
        log.info("transforming for REFERRAL ids {}", referralList.stream()
                .map(Referral::getId).collect(Collectors.toList()));
        List<ReferralIndexV1> referralIndexV1List = referralList.stream()
                .map(this::transform)
                .collect(Collectors.toList());
        log.info("transformation success for REFERRAL id's {}", referralIndexV1List.stream()
                .map(ReferralIndexV1::getReferral)
                .map(Referral::getId)
                .collect(Collectors.toList()));
        producer.push(topic, referralIndexV1List);
    }

    public ReferralIndexV1 transform(Referral referral) {
        String tenantId = referral.getTenantId();
        List<ProjectBeneficiary> projectBeneficiaryList = projectService.searchBeneficiary(referral.getProjectBeneficiaryClientReferenceId(), tenantId);
        ProjectBeneficiary projectBeneficiary = !CollectionUtils.isEmpty(projectBeneficiaryList) ? projectBeneficiaryList.get(0) : null;
        Map<String, Object> individualDetails = new HashMap<>();
        Map<String, String> boundaryHierarchy = new HashMap<>();
        Project project;

        String projectTypeId = null;
        if (projectBeneficiary != null) {
            individualDetails = individualService.findIndividualByClientReferenceId(projectBeneficiary.getBeneficiaryClientReferenceId(), tenantId);
            String projectId = projectBeneficiary.getProjectId();
            project = projectService.getProject(projectId, tenantId);
            projectTypeId = project.getProjectTypeId();
            if (individualDetails.containsKey(ADDRESS_CODE)) {
                boundaryHierarchy = commonUtils.getBoundaryHierarchyWithLocalityCode((String) individualDetails.get(ADDRESS_CODE), tenantId);
            } else {
                boundaryHierarchy = commonUtils.getBoundaryHierarchyWithLocalityCode(projectId, tenantId);
            }
        }
        String facilityName = Optional.of(referral)
                .filter(r -> FACILITY.equalsIgnoreCase(r.getRecipientType()))
                .map(r -> facilityService.findFacilityById(r.getRecipientId(), tenantId))
                .map(Facility::getName)
                .orElse(DEFAULT_FACILITY_NAME);

        Map<String, String> userInfoMap = userService.getUserInfo(tenantId, referral.getAuditDetails().getCreatedBy());

        Integer cycleIndex = commonUtils.fetchCycleIndex(tenantId, projectTypeId, referral.getAuditDetails());
        ObjectNode additionalDetails = objectMapper.createObjectNode();
        additionalDetails.put(CYCLE_INDEX, cycleIndex);

        ReferralIndexV1 referralIndexV1 = ReferralIndexV1.builder()
                .referral(referral)
                .tenantId(referral.getTenantId())
                .userName(userInfoMap.get(USERNAME))
                .nameOfUser(userInfoMap.get(NAME))
                .role(userInfoMap.get(ROLE))
                .userAddress(userInfoMap.get(CITY))
                .facilityName(facilityName)
                .age(individualDetails.containsKey(AGE) ? (Integer) individualDetails.get(AGE) : null)
                .dateOfBirth(individualDetails.containsKey(DATE_OF_BIRTH) ? (Long) individualDetails.get(DATE_OF_BIRTH) : null)
                .individualId(individualDetails.containsKey(INDIVIDUAL_ID) ? (String) individualDetails.get(INDIVIDUAL_ID) : null)
                .gender(individualDetails.containsKey(GENDER) ? (String) individualDetails.get(GENDER) : null)
                .boundaryHierarchy(boundaryHierarchy)
                .taskDates(commonUtils.getDateFromEpoch(referral.getClientAuditDetails().getLastModifiedTime()))
                .syncedDate(commonUtils.getDateFromEpoch(referral.getAuditDetails().getLastModifiedTime()))
                .additionalDetails(additionalDetails)
                .build();

        return referralIndexV1;
    }
}
