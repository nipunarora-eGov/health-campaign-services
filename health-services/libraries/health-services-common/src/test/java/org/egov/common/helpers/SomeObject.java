package org.egov.common.helpers;

import digit.models.coremodels.AuditDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.data.query.annotations.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="some_table")
public class SomeObject {
    private String id;
    private String otherField;
    private Integer rowVersion;
    private String tenantId;
    private Boolean isDeleted;
    private AuditDetails auditDetails;
}