package bobr.mainMicroservice.camunda;

import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExternalService {

    private final UserService userService;
    private final AuthenticationExternalService authExternalService;
    private final ExternalTaskClientService externalTaskClientService;

    @PostConstruct
    public void addCamundaTaskHandlers() {
        externalTaskClientService.addHandlerToTopic("sub-renew", this::renew);
        externalTaskClientService.addHandlerToTopic("sub-buy", this::buySubscription);
        externalTaskClientService.addHandlerToTopic("sub-info", this::isUserHaveSubscription);
    }

    private void renew(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        userService
                .findUsersWithLessThanOneDaySubscription()
                .forEach(userService::buySubscription);

        externalTaskService.complete(externalTask);
        log.info("Subscriptions updated");
    }

    private void buySubscription(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        User user = authExternalService.getTaskUser(externalTask);

        try {
            userService.buySubscription(user);
            externalTaskService.complete(externalTask);
        } catch (Exception e) {
            externalTaskService.handleBpmnError(externalTask, "subError");
        }
    }

    private void isUserHaveSubscription(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        User user = authExternalService.getTaskUser(externalTask);

        externalTaskService.complete(
                externalTask,
                Collections.singletonMap("isUserSubscriber", user.haveSubscription())
        );
    }

}
