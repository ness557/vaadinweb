package simlpe.microservices.vaadinweb.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class RestUserActivityService implements UserActivityService {

    private RestTemplate restTemplate;

    @Value("${store.host}")
    private String host;

    @Value("${store.activity.url}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public RestUserActivityService(){restTemplate = new RestTemplate();}

    @Override
    public void notifyActivity(String username, String token) {
        logger.info("Notifying user activity, username: " + username);

        String req = host + url;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("authorization", "bearer:" + token);

        HttpEntity entity = new HttpEntity<>(username, headers);

        try {
            ResponseEntity responseEntity = restTemplate.exchange(req, HttpMethod.POST, entity, byte[].class);
        } catch (RestClientException e) {
        }
    }
}
