package api;

public class RegisterResponseSuccess {
    private Integer id;
    private String token;

    public RegisterResponseSuccess(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
