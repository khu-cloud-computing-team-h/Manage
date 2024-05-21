package cloudcomputing.jhs.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public void saveImage(String imageID, String userID, String s3url){

        Image image = new Image();

        image.setImageID(imageID);
        image.setUserID(userID);
        image.setS3url(s3url);

        imageRepository.save(image);
    }
}
