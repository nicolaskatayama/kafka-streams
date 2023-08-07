package br.com.coffeeandit.config;

import br.com.coffeeandit.KafkaTestBase;
import br.com.coffeeandit.event.Flight;
import br.com.coffeeandit.event.OtherFlight;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest(classes = FlightStream.class)
class FlightStreamTest extends KafkaTestBase {

    private final String inputTopic;
    private final String outputTopic;

    FlightStreamTest(@Value("${spring.kafka.topic}") String inputTopic,
                     @Value("${spring.kafka.othertopic}") String outputTopic) {

        this.inputTopic = inputTopic;
        this.outputTopic = outputTopic;
    }

    @Test
    @DisplayName("Deve testar FlightStream topologia")
    void deveTestarFlightStreamTopologia() throws Exception {

        var consumer = criarConsumerESeInscrever(getEmbeddedKafkaBroker().getBrokersAsString(), outputTopic, OtherFlight.class);

        var flight = Flight
                .builder()
                .id("1")
                .origin("Poa")
                .destination("SP")
                .name("teste")
                .build();

        getKafkaTemplate().send(inputTopic, flight).get();

        var mensagensTopico = consumer.poll(Duration.ofSeconds(3));

        Assertions.assertTrue(mensagensTopico.iterator().hasNext());

        var valorTopico = mensagensTopico.iterator().next().value();

        assertAll(
                () -> Assertions.assertEquals(valorTopico.getId(), flight.getId()),
                () -> Assertions.assertEquals(valorTopico.getOrigin(), flight.getOrigin()),
                () -> Assertions.assertEquals(valorTopico.getDestination(), flight.getDestination()),
                () -> Assertions.assertEquals(valorTopico.getOtherName(), flight.getName())
        );

        consumer.close();

    }

}
