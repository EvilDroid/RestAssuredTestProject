package api;

public class UserJob {
    private String name;
    private String job;

    public UserJob(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public String getJob() {
        return job;
    }
}
