package cloudcomputing.jhs.Image;

import com.amazonaws.services.kms.model.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public String getS3UrlByImageId(Long imageId) {
        //이미지 ID로 이미지를 데이터베이스에서 찾기
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Image not found with ID: " + imageId));

        //이미지의 S3 URL을 반환
        return image.getS3url();
    }

    public Image getImageById(Long imageID) {
        Optional<Image> image = imageRepository.findById(imageID);
        return image.orElse(null);
    }

    public ResponseEntity<String> getImageJsonById(Long imageID) {
        //이미지 ID로 이미지 정보를 데이터베이스에서 조회
        Optional<Image> imageOptional = imageRepository.findById(imageID);

        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();

            //이미지 정보를 JSON 형식으로 변환하여 응답
            String json = "{ " +
                    "\"UserID\": \"" + image.getUserID() + "\", " +
                    "\"ImageID\": \"" + image.getImageID() + "\", " +
                    "\"Tags\": [], " + //태그 생성 로직이 없으므로 빈 배열을 사용
                    "\"UploadTime\": \"" + image.getUploadTime() + "\" " +
                    "}";

            return ResponseEntity.ok(json);
        } else {
            //해당 이미지 ID에 대한 이미지가 존재하지 않는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image info not found");
        }
    }

    public boolean deleteImage(Long imageID) {
        Optional<Image> optionalImage = imageRepository.findById(imageID);

        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();

            try {
                //파일 시스템에서 이미지 삭제
                Path imagePath = Paths.get(image.getS3url());
                Files.deleteIfExists(imagePath);

                //데이터베이스에서 이미지 정보 삭제
                imageRepository.deleteById(imageID);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
