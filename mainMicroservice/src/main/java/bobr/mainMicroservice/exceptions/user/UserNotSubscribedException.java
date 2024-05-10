package bobr.mainMicroservice.exceptions.user;

public class UserNotSubscribedException extends RuntimeException {

    public UserNotSubscribedException() {
        super("You are don't have a subscription");
    }

}
