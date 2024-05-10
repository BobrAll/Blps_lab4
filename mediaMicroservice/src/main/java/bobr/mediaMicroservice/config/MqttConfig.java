package bobr.mediaMicroservice.config;

import bobr.mediaMicroservice.image.Base64ImageService;
import bobr.mediaMicroservice.image.DownloadImageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;

@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final Base64ImageService imageService;

    @Value("${RABBITMQ_HOST}")
    private String rabbitmqHost;

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper();
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{rabbitmqHost});
        options.setUserName("guest");
        options.setPassword("guest".toCharArray());

        factory.setConnectionOptions(options);

        return factory;
    }


    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public IntegrationFlow mqttInFlow() {
        return IntegrationFlow.from(mqttInbound())
                .handle(message -> {
                    try {
                        DownloadImageRequest request = jsonMapper().readValue(
                                message.getPayload().toString(),
                                DownloadImageRequest.class
                        );

                        imageService.save(
                                Base64ImageService.download(
                                        request.getUrl(),
                                        request.getFlatId())
                        );
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                })
                .get();
    }

    @Bean
    public MessageProducerSupport mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("javaMqqtClient", mqttClientFactory(), "#");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);

        return adapter;
    }

}
