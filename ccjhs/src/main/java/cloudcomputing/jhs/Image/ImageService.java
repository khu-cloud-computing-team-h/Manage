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

        imageRepository.save(image);
    }

    public Image getImageById(Long imageID) {
        Optional<Image> image = imageRepository.findById(imageID);
        return image.orElse(null);
    }

    public ResponseEntity<String> getImageJsonById(Long imageID) {
        //이미지 ID로 이미지 정보를 데이터베이스에서 조회
        Image image = imageRepository.findById(imageID).orElse(null);

        if (image != null) {
            //이미지 정보를 JSON 형식으로 변환하여 응답
            String json = "{ " +
                    "\"name\": \"" + image.getUserID() + "\", " +
                    "\"imageId\": \"" + image.getImageID() + "\", " +
                    "\"tags\": [], " + //아직 태그 생성 로직이 없으므로 빈 배열을 사용
                    "\"uploadTime\": \"" + image.getUploadTime() + "\" " +
                    "}";

            return ResponseEntity.ok(json);
        } else {
            //해당 이미지 ID에 대한 이미지가 존재하지 않는 경우
            return ResponseEntity.notFound().build();
        }
    }
}
