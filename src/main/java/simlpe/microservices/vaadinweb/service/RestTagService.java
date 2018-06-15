package simlpe.microservices.vaadinweb.service;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import simlpe.microservices.vaadinweb.model.Tag;

import java.util.*;

@Service
public class RestTagService implements TagService {

    private RestTemplate restTemplate;

    @Value("${tags.host}")
    private String host;

    @Value("${tags.url}")
    private String url;

    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public RestTagService(){
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Set<Tag> getTags() {
        logger.info("Trying to get tags");

        String req = host + url;

        try {
            ResponseEntity responseEntity = restTemplate.getForEntity(req, Tag[].class);

            if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {

                Tag[] tags =(Tag[]) responseEntity.getBody();
                Set<Tag> taglist = new HashSet<>();

                Collections.addAll(taglist, tags);

                logger.info("Got tags: " + taglist);
                return taglist;
            }
        } catch (RestClientException e) {
        }

        logger.info("Error");
        return null;
    }
}