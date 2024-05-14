package bobr.mainMicroservice.camunda;

import bobr.mainMicroservice.config.MqttConfig;
import bobr.mainMicroservice.image.DownloadImageRequest;
import bobr.mainMicroservice.realty.flat.FlatAddRequest;
import bobr.mainMicroservice.realty.flat.FlatService;
import bobr.mainMicroservice.user.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FlatExternalService {

    private final FlatService flatService;
    private final CamundaMapper camundaMapper;
    private final AuthenticationExternalService authExternalService;
    private final ExternalTaskClientService externalTaskClientService;

    private final MqttConfig.MQTTGateway mqttGateway;
    private final CamundaMapper mapper;

    @PostConstruct
    public void addCamundaTaskHandlers() {
        externalTaskClientService.addHandlerToTopic("flat-save", this::saveFlat);
        externalTaskClientService.addHandlerToTopic("flat-remove", this::removeFlat);
        externalTaskClientService.addHandlerToTopic("flat-images-send", this::sendFlatImages);
    }

    private void saveFlat(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();
        User user = authExternalService.getTaskUser(externalTask);

        FlatAddRequest flat = FlatAddRequest.builder()
                .totalArea(mapper.readDouble(vars, "totalArea"))
                .totalPrice(mapper.readDouble(vars, "totalPrice"))
                .address(mapper.readString(vars, "address"))
                .ownerId(user.getId())
                .isBoosted(mapper.readBool(vars, "isBoosted"))
                .haveBalcony(mapper.readBool(vars, "haveBalcony"))
                .isApartments(mapper.readBool(vars, "isApartments"))
                .kitchenArea(mapper.readDouble(vars, "kitchenArea"))
                .livingArea(mapper.readDouble(vars, "livingArea"))
                .rooms(mapper.readLongAsInt(vars, "rooms"))
                .floor(mapper.readLongAsInt(vars, "floor"))
                .build();

        Integer flatId = flatService.save(flat).getId();

        externalTaskService.complete(
                externalTask,
                Collections.singletonMap("flatId", flatId)
        );
    }

    private void removeFlat(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Integer flatId = camundaMapper.readInt(externalTask.getAllVariables(), "flatId");
        flatService.delete(flatId);

        externalTaskService.complete(externalTask);
    }


    private void sendFlatImages(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        ObjectMapper jsonMapper = new ObjectMapper();
        Map<String, Object> vars = externalTask.getAllVariables();

        Integer flatId = camundaMapper.readInt(vars, "flatId");
        String imagesJsonArray = camundaMapper.readString(vars, "imageUrls");

        try {
            String[] imageUrls = jsonMapper.readValue(imagesJsonArray, String[].class);
            Arrays.stream(imageUrls)
                    .forEach(url -> {
                                try {
                                    mqttGateway.sendToMqtt(jsonMapper.writeValueAsString(new DownloadImageRequest(url, flatId)));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        externalTaskService.complete(externalTask);
    }

}
