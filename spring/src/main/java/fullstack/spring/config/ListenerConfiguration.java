package fullstack.spring.config;

import fullstack.spring.dto.ChatRequestDTO;
import fullstack.spring.dto.ChatResponseDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
public class ListenerConfiguration {
    ApplicationContext ac = new AnnotationConfigApplicationContext(ConsumerConfiguration.class);
    ConsumerFactory consumerFactory = ac.getBean(ConsumerFactory.class);

    // KafkaListener 컨테이너 팩토리를 생성
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ChatResponseDTO> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
