package simlpe.microservices.vaadinweb.service;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import simlpe.microservices.vaadinweb.model.Poem;

import java.util.Collections;

@Service
public class RestPoemService implements PoemService {

    private RestTemplate restTemplate;

    @Value("${store.host}")
    private String host;

    @Value("${store.poem.url}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public RestPoemService() {
        restTemplate = new RestTemplate();
    }

    @Override
    public boolean savePoem(Poem poem, String token) {
        logger.info("Trying to save poem: " + poem);

        String req = host + url;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("authorization", "bearer:" + token);

        HttpEntity entity = new HttpEntity<>(poem, headers);

        try {
            ResponseEntity responseEntity = restTemplate.exchange(req, HttpMethod.PUT, entity, byte[].class);
            return responseEntity.getStatusCode().equals(HttpStatus.OK);
        } catch (RestClientException e) {
        }

        return false;
    }
}
