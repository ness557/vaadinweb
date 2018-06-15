package simlpe.microservices.vaadinweb.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestTokenService implements TokenService{

    private RestTemplate restTemplate;

    @Value("${token.auth.host}")
    private String authHots;

    @Value("${token.auth.url}")
    private String tokenUrl;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(getClass().getName());

    public RestTokenService(){
        restTemplate = new RestTemplate();
    }

    @Override
    public String getToken(String username, String password) {

        logger.info("Trying to get token by username: " + username);

        String req = authHots + tokenUrl
                + "?username=" + username
                + "&password=" + password;

        ResponseEntity responseEntity = restTemplate.getForEntity(req, String.class);

        if(responseEntity.getStatusCode().equals(HttpStatus.OK)){

            logger.info("Got token");
            return (String) responseEntity.getBody();
        }

        logger.info("Didn't get token");
        return null;
    }
}
