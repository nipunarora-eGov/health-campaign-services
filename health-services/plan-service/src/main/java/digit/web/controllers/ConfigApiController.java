package digit.web.controllers;


import digit.service.PlanConfigurationService;
import digit.web.models.PlanConfigurationRequest;
import digit.web.models.PlanConfigurationResponse;
import digit.web.models.PlanConfigurationSearchRequest;
import javax.validation.Valid;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-03-04T09:55:29.782094600+05:30[Asia/Calcutta]")
@Controller
@RequestMapping("/plan")
public class ConfigApiController {

    private final ObjectMapper objectMapper;

    private final PlanConfigurationService planConfigurationService;

    @Autowired
    public ConfigApiController(ObjectMapper objectMapper, PlanConfigurationService planConfigurationService) {
        this.objectMapper = objectMapper;
        this.planConfigurationService = planConfigurationService;
    }

    @RequestMapping(value = "/config/_create", method = RequestMethod.POST)
    public ResponseEntity<PlanConfigurationResponse> configCreatePost(@Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody PlanConfigurationRequest body) {

            PlanConfigurationRequest planConfigurationRequest = planConfigurationService.create(body);
            PlanConfigurationResponse response = PlanConfigurationResponse.builder()
                    .planConfigurationResponse(Collections.singletonList(planConfigurationRequest.getPlanConfiguration()))
                    .responseInfo(new ResponseInfo())
                    .build();

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

    }

    @RequestMapping(value = "/config/_search", method = RequestMethod.POST)
    public ResponseEntity<PlanConfigurationResponse> configSearchPost(@Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody PlanConfigurationSearchRequest body) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PlanConfigurationResponse());
    }

    @RequestMapping(value = "/config/_update", method = RequestMethod.POST)
    public ResponseEntity<PlanConfigurationResponse> configUpdatePost(@Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @Valid @RequestBody PlanConfigurationRequest body) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new PlanConfigurationResponse());
    }
}
