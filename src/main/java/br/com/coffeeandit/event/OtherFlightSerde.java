package br.com.coffeeandit.event;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class OtherFlightSerde implements Serde<OtherFlight> {

  private JsonSerializer<OtherFlight> serializer;
  private JsonDeserializer<OtherFlight> deserializer;

  public OtherFlightSerde() {
    this.serializer = new JsonSerializer<>(OtherFlight.class);
    this.deserializer = new JsonDeserializer<>(OtherFlight.class);
  }

  public Serializer<OtherFlight> serializer() {
    return serializer;
  }

  @Override
  public Deserializer<OtherFlight> deserializer() {
    return deserializer;
  }

}