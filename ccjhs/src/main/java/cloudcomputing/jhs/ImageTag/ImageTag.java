package cloudcomputing.jhs.ImageTag;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Entity(name = "ImageTag")
public class ImageTag {

    @EmbeddedId
    private ImageTagPK pk;

    public ImageTag() {}

    public ImageTag(Long tagID, Long imageID) {
        this.pk = new ImageTagPK(tagID, imageID);
    }

    public ImageTagPK getPk() {
        return pk;
    }

    public void setPk(ImageTagPK pk) {
        this.pk = pk;
    }
}
