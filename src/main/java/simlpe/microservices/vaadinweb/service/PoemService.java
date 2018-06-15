package simlpe.microservices.vaadinweb.service;

import simlpe.microservices.vaadinweb.model.Poem;

public interface PoemService {
    boolean savePoem(Poem poem, String token);
}
