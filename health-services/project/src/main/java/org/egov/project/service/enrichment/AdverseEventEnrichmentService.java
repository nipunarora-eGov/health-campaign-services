package org.egov.project.service.enrichment;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.models.project.AdverseEvent;
import org.egov.common.models.project.AdverseEventBulkRequest;
import org.egov.common.models.project.ProjectResourceBulkRequest;
import org.egov.common.service.IdGenService;
import org.egov.common.utils.CommonUtils;
import org.egov.project.config.ProjectConfiguration;
import org.egov.project.repository.AdverseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.egov.common.utils.CommonUtils.*;

@Component
@Slf4j
public class AdverseEventEnrichmentService {

    private final IdGenService idGenService;

    private final ProjectConfiguration projectConfiguration;

    private final AdverseEventRepository adverseEventRepository;

    public AdverseEventEnrichmentService(IdGenService idGenService, ProjectConfiguration projectConfiguration, AdverseEventRepository adverseEventRepository) {
        this.idGenService = idGenService;
        this.projectConfiguration = projectConfiguration;
        this.adverseEventRepository = adverseEventRepository;
    }

    public void create(List<AdverseEvent> entities, AdverseEventBulkRequest request) throws Exception {
        log.info("starting the enrichment for create adverse event");
        log.info("generating IDs using UUID");
        List<String> idList = CommonUtils.uuidSupplier().apply(entities.size());
        log.info("enriching adverse events with generated IDs");
        enrichForCreate(entities, idList, request.getRequestInfo());
        log.info("enrichment done");
    }

    public void update(List<AdverseEvent> entities, AdverseEventBulkRequest request) {
        log.info("starting the enrichment for create adverse event");
        Map<String, AdverseEvent> adverseEventMap = getIdToObjMap(entities);
        enrichForUpdate(adverseEventMap, entities, request);
        log.info("enrichment done");
    }

    public void delete(List<AdverseEvent> entities, AdverseEventBulkRequest request) {
        log.info("starting the enrichment for delete adverse event");
        enrichForDelete(entities, request.getRequestInfo(), true);
        log.info("enrichment done");
    }
}