package cloudcomputing.jhs.ImageTag;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ImageTagPK implements Serializable {

    @Column(name = "TagID")
    private Long tagID;

    @Column(name = "ImageID")
    private Long imageID;
}
