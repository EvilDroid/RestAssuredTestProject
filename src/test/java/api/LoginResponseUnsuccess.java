package api;

public class LoginResponseUnsuccess {
    private String error;

    public LoginResponseUnsuccess(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
