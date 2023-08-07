package br.com.coffeeandit;

import br.com.coffeeandit.config.KafkaSettings;
import lombok.Getter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;


@Getter
@ContextConfiguration(classes = {KafkaSettings.class, KafkaAutoConfiguration.class})
@EmbeddedKafka(
        topics = {"topic", "othertopic"},
        partitions = 1,
        controlledShutdown = true,
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = "log.dir=target/${random.uuid}/embedded-kafka")
@Import(KafkaTestBase.KafkaTesteConfig.class)
public abstract class KafkaTestBase {

    private static final String EARLIEST = "earliest";
    private static final String FALSE = "false";
    private static final String ASTERISCO = "*";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    public static <T> Consumer<String, T> criarConsumerESeInscrever(String broker, String topic, Class<T> classe){
        Consumer<String, T> consumer = novo(broker, classe);
        consumer.subscribe(Collections.singletonList(topic));
        return consumer;
    }

    private static <T> Consumer<String, T> novo(String brokersLocation, Class<T> clazz) {

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps( brokersLocation, UUID.randomUUID().toString(), FALSE);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);

        JsonDeserializer<T> deserializer = new JsonDeserializer(clazz, false);
        deserializer.addTrustedPackages(ASTERISCO);

        ConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), deserializer);
        return consumerFactory.createConsumer();
    }

    @TestConfiguration
    public static class KafkaTesteConfig {
        @Bean
        @Primary
        public KafkaTemplate<String, Object> kafkaTemplateObject(EmbeddedKafkaBroker embeddedKafkaBroker) {
            Map<String, Object> props = KafkaTestUtils.producerProps(embeddedKafkaBroker);
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
            return new KafkaTemplate<String, Object>(new DefaultKafkaProducerFactory(props));
        }
    }

}
