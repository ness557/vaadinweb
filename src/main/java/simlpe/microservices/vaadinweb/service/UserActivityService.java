package simlpe.microservices.vaadinweb.service;

public interface UserActivityService {
    void notifyActivity(String username, String token);
}
