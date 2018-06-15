package simlpe.microservices.vaadinweb.service;

public interface TokenService {
    String getToken(String username, String password);
}
