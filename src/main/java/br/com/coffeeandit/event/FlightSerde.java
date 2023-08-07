package br.com.coffeeandit.event;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FlightSerde implements Serde<Flight> {

  private JsonSerializer<Flight> serializer;
  private JsonDeserializer<Flight> deserializer;

  public FlightSerde() {
    this.serializer = new JsonSerializer<>(Flight.class);
    this.deserializer = new JsonDeserializer<>(Flight.class);
  }

  public Serializer<Flight> serializer() {
    return serializer;
  }

  @Override
  public Deserializer<Flight> deserializer() {
    return deserializer;
  }

}