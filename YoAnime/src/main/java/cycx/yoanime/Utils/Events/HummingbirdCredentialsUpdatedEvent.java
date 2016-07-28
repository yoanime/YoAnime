package cycx.yoanime.Utils.Events;

public class HummingbirdCredentialsUpdatedEvent {
    public final String usernameOrEmail;
    public final String password;

    public HummingbirdCredentialsUpdatedEvent (String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }
}
