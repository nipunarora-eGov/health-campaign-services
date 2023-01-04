package org.egov.individual.helper;

import org.egov.individual.web.models.Gender;
import org.egov.individual.web.models.Identifier;
import org.egov.individual.web.models.IndividualSearch;
import org.egov.individual.web.models.Name;

import java.time.LocalDate;

public class IndividualSearchTestBuilder {
    private IndividualSearch.IndividualSearchBuilder builder;

    public IndividualSearchTestBuilder() {
        this.builder = IndividualSearch.builder();
    }

    public static IndividualSearchTestBuilder builder() {
        return new IndividualSearchTestBuilder();
    }

    public IndividualSearch build() {
        return this.builder.build();
    }

    public IndividualSearchTestBuilder byId(String... args) {
        this.builder.id(args != null && args.length > 0 ? args[0] : "some-id");
        return this;
    }

    public IndividualSearchTestBuilder byClientReferenceId(String... args) {
        this.builder.clientReferenceId(args != null && args.length > 0 ? args[0] : "some-client-reference-id");
        return this;
    }


    public IndividualSearchTestBuilder byName() {
        this.builder.name(Name.builder()
                        .givenName("some-given-name")
                        .familyName("some-family-name")
                        .otherNames("some-other-name")
                .build());
        return this;
    }

    public IndividualSearchTestBuilder byGender(String... args) {
        this.builder.gender(args != null && args.length > 0 ?
                Gender.valueOf(args[0]) : Gender.MALE);
        return this;
    }

    public IndividualSearchTestBuilder byDateOfBirth(LocalDate... args) {
        this.builder.dateOfBirth(args != null && args.length > 0 ?
                args[0] : LocalDate.now());
        return this;
    }

    public IndividualSearchTestBuilder byIdentifier() {
        this.builder.identifier(Identifier.builder()
                        .identifierId("some-identifier-id")
                        .identifierType("SYSTEM_GENERATED")
                .build());
        return this;
    }

    public IndividualSearchTestBuilder byBoundaryCode() {
        this.builder.boundaryCode("some-boundary-code");
        return this;
    }
}