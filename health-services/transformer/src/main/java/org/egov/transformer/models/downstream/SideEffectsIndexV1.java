package org.egov.transformer.models.downstream;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.Valid;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SideEffectsIndexV1 {

    @JsonProperty("id")
    private String id;

    @JsonProperty("clientReferenceId")
    private String clientReferenceId;

    @JsonProperty("taskId")
    private String taskId;

    @JsonProperty("taskClientReferenceId")
    private String taskClientReferenceId;

    @JsonProperty("projectBeneficiaryId")
    private String projectBeneficiaryId;

    @JsonProperty("projectBeneficiaryClientReferenceId")
    private String projectBeneficiaryClientReferenceId;

    @JsonProperty("dateOfBirth")
    private Long dateOfBirth;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("symptoms")
    private List<String> symptoms;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("isDeleted")
    private Boolean isDeleted = Boolean.FALSE;

    @JsonProperty("rowVersion")
    private Integer rowVersion;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("clientAuditDetails")
    private AuditDetails clientAuditDetails;

    @JsonProperty("boundaryHierarchy")
    private ObjectNode boundaryHierarchy;

    @JsonProperty("additionalDetails")
    private ObjectNode additionalDetails;
}