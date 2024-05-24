package cloudcomputing.jhs.Image;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
//@Table(name = "Image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ImageID")
    private Long imageID;

    @Column(name = "UserID")
    private BigDecimal userID;

    @Column(name = "S3URL")
    private String s3url;

    @CreationTimestamp
    @Column(name = "UploadTime")
    private Timestamp uploadTime;

}
