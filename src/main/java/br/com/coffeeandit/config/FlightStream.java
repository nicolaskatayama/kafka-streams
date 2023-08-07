package br.com.coffeeandit.config;

import br.com.coffeeandit.event.Flight;
import br.com.coffeeandit.event.FlightSerde;
import br.com.coffeeandit.event.OtherFlight;
import br.com.coffeeandit.event.OtherFlightSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
class FlightStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlightStream.class);
    private final String topic;
    private final String otherTopic;

    FlightStream(@Value("${spring.kafka.topic}") String topic,
                 @Value("${spring.kafka.othertopic}") String otherTopic) {

        this.topic = topic;
        this.otherTopic = otherTopic;
    }

    private void salvar(String s, Flight flight) {
        LOGGER.info("salvando entidade: {} ", s);
    }

    @Bean
    KStream<String, OtherFlight> flightKStream(StreamsBuilder streamsBuilder) {

        KStream<String, OtherFlight> outputStream = streamsBuilder.stream(
                topic,
                Consumed.with(Serdes.String(), new FlightSerde())
        ).map(
                this::checkId
        ).filter(
                this::filterIdNotNull
        ).peek(
                (k,v) -> LOGGER.info("Consumindo tópico: {}, {}", k, v)
        ).peek(
                this::salvar
        ).map(
                this::converterOtherFlight
        ).peek(
                (k,v) -> LOGGER.info("publicando em outro tópico: {}, {}", k, v)
        );

        outputStream.to(otherTopic, Produced.with(Serdes.String(), new OtherFlightSerde()));

        return outputStream;

    }

    private KeyValue<String, OtherFlight> converterOtherFlight(String chave, Flight flight) {
        OtherFlight otherFlight = OtherFlight.convert(flight);
        return new KeyValue<>(chave, otherFlight);
    }

    private Boolean filterIdNotNull(String key, Flight value) {
        if (key == null) {
            LOGGER.info("id null: {}", value.getName());
        }
        return key != null;
    }

    private KeyValue<String, Flight> checkId(String key,
                                             Flight value) {
        return new KeyValue<>(
                Optional.ofNullable(value.getId())
                        .map(String::valueOf)
                        .orElse(key),
                value
        );
    }


}
