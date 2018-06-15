package simlpe.microservices.vaadinweb.model;

import lombok.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Set;

@XmlRootElement
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode @ToString
public class Poem {

    private String username;

    private String data;

    private Set<Tag> tags;

    public void addTag(Tag tag){
        tags.add(tag);
    }
}
