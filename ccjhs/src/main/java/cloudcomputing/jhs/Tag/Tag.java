package cloudcomputing.jhs.Tag;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TagID")
    private Long tagID;

    @Column(name = "TagName")
    private String tagName;
}
