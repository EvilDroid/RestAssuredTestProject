package api;

public class RegisterResponseUnsuccess {
    private String error;

    public RegisterResponseUnsuccess(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
