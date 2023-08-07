package br.com.coffeeandit.event;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Flight {

    String id;
    String origin;
    String destination;
    String name;

}
