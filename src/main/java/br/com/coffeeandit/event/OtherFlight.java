package br.com.coffeeandit.event;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OtherFlight {

    String id;
    String origin;
    String destination;
    String otherName;

    public static OtherFlight convert(Flight flight) {
        return OtherFlight.builder()
                .id(flight.getId())
                .origin(flight.getOrigin())
                .otherName(flight.getName())
                .destination(flight.getDestination())
                .build();
    }

}
