package bobr.mainMicroservice.camunda;

import org.camunda.bpm.client.ExternalTaskClient;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ExternalTaskClientService {

    @Value("${CAMUNDA_URL}")
    private String camundaUrl;
    private static final int RESPONSE_TIMEOUT = 10000;
    private static final int LOCK_DURATION = 1000;

    public void addHandlerToTopic(String topic, ExternalTaskHandler handler) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(camundaUrl)
                .asyncResponseTimeout(RESPONSE_TIMEOUT)
                .build();

        client.subscribe(topic)
                .lockDuration(LOCK_DURATION)
                .handler(handler)
                .open();
    }

}
