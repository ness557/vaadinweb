package simlpe.microservices.vaadinweb.service;

import simlpe.microservices.vaadinweb.model.Tag;

import java.util.Set;

public interface TagService {
    Set<Tag> getTags();
}
