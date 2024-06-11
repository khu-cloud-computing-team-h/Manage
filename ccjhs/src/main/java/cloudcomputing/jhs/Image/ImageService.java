package cloudcomputing.jhs.Image;

import cloudcomputing.jhs.ImageTag.ImageTagRepository;
import cloudcomputing.jhs.S3.S3Service;
import cloudcomputing.jhs.Tag.Tag;
import cloudcomputing.jhs.Tag.TagRepository;
import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.yaml.snakeyaml.tokens.Token.ID.Tag;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ImageTagRepository imageTagRepository;

    @Autowired
    private TagRepository tagRepository;

    public Image saveImage(BigDecimal userID, String uploadPath) {
        Image image = new Image();

        image.setUserID(userID);
        image.setS3url(uploadPath);

        return imageRepository.save(image);
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
                    "\"Tags\": " + getTagsJsonByImageId(imageID) + ", " +
                    "\"UploadTime\": \"" + image.getUploadTime() + "\" " +
                    "}";

            return ResponseEntity.ok(json);
        } else {
            //해당 이미지 ID에 대한 이미지가 존재하지 않는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image info not found");
        }
    }

    public ResponseEntity<String> getAllImagesJsonByUserId(BigDecimal userID) {
        List<Image> images = imageRepository.findByUserID(userID);

        if (images.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body("Image info is empty.");
        }

        StringBuilder jsonBuilder = new StringBuilder();

        for (Image image : images) {
            String json = "{ " +
                    "\"UserID\": \"" + image.getUserID() + "\", " +
                    "\"ImageID\": \"" + image.getImageID() + "\", " +
                    "\"Tags\": " + getTagsJsonByImageId(image.getImageID()) + ", " +
                    "\"UploadTime\": \"" + image.getUploadTime() + "\" " +
                    "  },\n";
            jsonBuilder.append(json);
        }

        //Remove the trailing comma and newline
        if (jsonBuilder.length() > 2) {
            jsonBuilder.setLength(jsonBuilder.length() - 2);
        }
        return ResponseEntity.ok(jsonBuilder.toString());
    }

    private String getTagsJsonByImageId(Long imageId) {
        // 이미지 ID로 이미지에 속한 태그들을 조회하여 JSON 배열 형태로 반환
        List<String> tags = imageTagRepository.findAllByPkImageID(imageId)
                .stream()
                .map(imageTag -> tagRepository.findById(imageTag.getPk().getTagID()).orElse(null))
                .filter(Objects::nonNull)
                .map(cloudcomputing.jhs.Tag.Tag::getTagName)
                .collect(Collectors.toList());

        return "[" + String.join(", ", tags) + "]";
    }

    public boolean deleteImage(Long imageID) {
        Optional<Image> optionalImage = imageRepository.findById(imageID);

        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();

            try {
                //S3에서 이미지 삭제
                s3Service.deleteS3Image(image.getS3url());

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

    public void updateImageName(Long imageID, String newName) {
        Optional<Image> optionalImage = imageRepository.findById(imageID);

        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();

            String oldUrl = image.getS3url();

            String newUrl = oldUrl.substring(0, oldUrl.lastIndexOf("/") + 1) + newName;

            try {
                s3Service.renameS3Image(oldUrl, newUrl);

                image.setS3url(newUrl);

                imageRepository.save(image);

            } catch (AmazonS3Exception e) {
                e.printStackTrace();

                throw new RuntimeException("Failed to update image name in S3");
            }
        } else {
            throw new NotFoundException("Image not found with ID: " + imageID);
        }
    }

    //이미지가 존재하는지 확인하는 메서드
    public boolean existsById(Long imageId) {
        Optional<Image> imageOptional = imageRepository.findById(imageId);

        return imageOptional.isPresent();
    }

    public List<String> getAllImageUrls(BigDecimal userId) {
        List<Image> images = imageRepository.findByUserID(userId);
        List<String> imageUrls = new ArrayList<>();

        for (Image image : images) {
            String imageUrl = image.getS3url();
            imageUrls.add(imageUrl);
        }
        return imageUrls;
    }
}
