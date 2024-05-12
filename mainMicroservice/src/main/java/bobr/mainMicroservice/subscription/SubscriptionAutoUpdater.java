package bobr.mainMicroservice.subscription;

import bobr.mainMicroservice.camunda.ExternalTaskClientService;
import bobr.mainMicroservice.user.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionAutoUpdater {

    private final UserService userService;
    private final ExternalTaskClientService externalTaskClientService;

    @PostConstruct
    public void addCamundaTaskHandler() {
        externalTaskClientService.addHandlerToTopic("sub-renewing", this::handleExternalTask);
    }

    private void handleExternalTask(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        userService
                .findUsersWithLessThanOneDaySubscription()
                .forEach(userService::buySubscription);

        externalTaskService.complete(externalTask);
        log.info("Subscriptions updated");
    }

}
