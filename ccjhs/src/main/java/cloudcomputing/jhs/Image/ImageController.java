package cloudcomputing.jhs.Image;

import cloudcomputing.jhs.S3.S3Service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private S3Service s3Service;

    @PostMapping("/api/manage/image")
    public ResponseEntity<String> uploadImage(@RequestParam("userID") String userID, @RequestParam("imageFile") MultipartFile imageFile) {

        try {
            //S3에 이미지 업로드
            String imageUrl = s3Service.uploadFile(imageFile);

            //MySQL에 이미지 정보 저장
            BigDecimal userIdBigDecimal = new BigDecimal(userID);
            //imageID는 자동 생성
            imageService.saveImage(null, userIdBigDecimal, imageUrl);

            //이미지 저장 후 이미지 URL 반환
            return ResponseEntity.ok().body("Image upload success. Image URL: " + imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Image upload Fail");
        }
    }

    @GetMapping("/api/manage/image/{imageID}")
    public ResponseEntity<Resource> getImage(@PathVariable("imageID") Long imageID) {

        Image image = imageService.getImageById(imageID);

        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        File imageFile = new File(image.getS3url());

        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            Resource resource = new ByteArrayResource(imageBytes);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imageFile.getName() + "\"")
                    .contentLength(imageFile.length())
                    .contentType(org.springframework.http.MediaType.IMAGE_JPEG)  //적절한 MIME 타입 설정
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/api/manage/image-data/{imageID}")
    public ResponseEntity<String> getImageData(@PathVariable Long imageID) {
        return imageService.getImageJsonById(imageID);
    }

    @DeleteMapping("/api/manage/image/{imageID}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageID) {
        boolean isDeleted = imageService.deleteImage(imageID);

        if (isDeleted) {
            return ResponseEntity.ok("Image delete success");
        } else {
            return ResponseEntity.status(404).body("Image not found or failed to delete");
        }
    }
}
