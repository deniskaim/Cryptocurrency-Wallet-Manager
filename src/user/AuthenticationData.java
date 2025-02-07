package user;

import java.io.Serial;
import java.io.Serializable;

public class AuthenticationData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567891234567L;

    private String username;
    private String password;

    public AuthenticationData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
