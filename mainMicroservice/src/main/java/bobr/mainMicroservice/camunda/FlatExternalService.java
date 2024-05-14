package bobr.mainMicroservice.camunda;

import bobr.mainMicroservice.realty.flat.FlatAddRequest;
import bobr.mainMicroservice.realty.flat.FlatService;
import bobr.mainMicroservice.security.jwt.JwtService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlatExternalService {

    private final JwtService jwtService;
    private final FlatService flatService;
    private final AuthenticationExternalService authExternalService;
    private final ExternalTaskClientService externalTaskClientService;

    private final CamundaMapper mapper;

    @PostConstruct
    public void addCamundaTaskHandlers() {
        externalTaskClientService.addHandlerToTopic("save-flat", this::saveFlat);
    }

    private void saveFlat(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        Map<String, Object> vars = externalTask.getAllVariables();

        String jwt = authExternalService.getJwts().get(vars.get("currentUser").toString());
        Integer userId = Integer.valueOf(jwtService.extractId(jwt));

        FlatAddRequest flat = FlatAddRequest.builder()
                .totalArea(mapper.readDouble(vars, "totalArea"))
                .totalPrice(mapper.readDouble(vars, "totalPrice"))
                .address(mapper.readString(vars, "address"))
                .ownerId(userId)
                .isBoosted(mapper.readBool(vars, "isBoosted"))
                .haveBalcony(mapper.readBool(vars, "haveBalcony"))
                .isApartments(mapper.readBool(vars, "isApartments"))
                .kitchenArea(mapper.readDouble(vars, "kitchenArea"))
                .livingArea(mapper.readDouble(vars, "livingArea"))
                .rooms(mapper.readInt(vars, "rooms"))
                .floor(mapper.readInt(vars, "floor"))
                .build();

        flatService.save(flat);
        externalTaskService.complete(externalTask);
    }

}
