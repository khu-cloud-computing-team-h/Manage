package cloudcomputing.jhs.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public void saveImage(Long imageID, BigDecimal userID, String uploadPath) {
        Image image = new Image();

        image.setImageID(imageID);
        image.setUserID(userID);
        image.setS3url(uploadPath);
        image.setUploadTime(new Timestamp(System.currentTimeMillis()));

        imageRepository.save(image);
    }

    public Image getImageById(Long imageID) {
        Optional<Image> image = imageRepository.findById(imageID);
        return image.orElse(null);
    }

    public ResponseEntity<String> getImageJsonById(Long imageID) {
        Optional<Image> imageOptional = imageRepository.findById(imageID);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            String json = "{ \"name\": \"" + image.getUserID() + "\", " +
                    "\"imageId\": \"" + image.getImageID() + "\", " +
                    "\"tags\": [], " + // 태그는 없으니 빈 배열로
                    "\"uploadTime\": \"" + image.getUploadTime() + "\" }";
            return ResponseEntity.ok(json);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
