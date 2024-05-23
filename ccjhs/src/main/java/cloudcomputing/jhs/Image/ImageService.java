package cloudcomputing.jhs.Image;

import org.springframework.beans.factory.annotation.Autowired;
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
}
