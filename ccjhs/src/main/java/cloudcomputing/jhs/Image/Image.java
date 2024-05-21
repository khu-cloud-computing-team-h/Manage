package cloudcomputing.jhs.Image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "image")
public class Image {

    @Id
    private String imageID;
    private String userID;
    private String s3url;

}
