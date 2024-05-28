package cloudcomputing.jhs.Image;

import cloudcomputing.jhs.S3.S3Service;
import cloudcomputing.jhs.dto.UpdateImageNameRequest;
import cloudcomputing.jhs.interceptor.TokenInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Member;
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

    // 사용자 토큰 사용하기 전의 기존 코드
//    @PostMapping("/api/manage/image")
//    public ResponseEntity<String> uploadImage(@RequestParam("userID") String userID, @RequestParam("imageFile") MultipartFile imageFile) {
//        try {
//            //S3에 이미지 업로드
//            String imageUrl = s3Service.uploadFile(imageFile);
//
//            //MySQL에 이미지 정보 저장
//            BigDecimal userIdBigDecimal = new BigDecimal(userID);
//            imageService.saveImage(null, userIdBigDecimal, imageUrl);
//
//            //이미지 저장 후 이미지 URL 반환
//            return ResponseEntity.ok().body("Image upload success. Image URL: " + imageUrl);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body("Image upload Fail");
//        }
//    }

    //토큰을 사용해 UserID를 가져와서 Image를 post하는 코드
    @PostMapping("/api/manage/image")
    public ResponseEntity<String> uploadImage(HttpServletRequest request, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            //토큰을 통해 추출한 사용자 ID 가져오기
            BigDecimal userIdBigDecimal = (BigDecimal) request.getAttribute("userId");

            //S3에 이미지 업로드
            String imageUrl = s3Service.uploadFile(imageFile);

            //MySQL에 이미지 정보 저장
            imageService.saveImage(null, userIdBigDecimal, imageUrl);

            //이미지 저장 후 이미지 URL 반환
            return ResponseEntity.ok().body("Image upload success. Image URL: " + imageUrl);
        } catch (IOException e) {
            e.printStackTrace();

            return ResponseEntity.badRequest().body("Image upload Fail");
        }
    }

    @GetMapping("/api/manage/image/{imageID}")
    public ResponseEntity<String> getImage(@PathVariable("imageID") Long imageID) {
        try {
            // 이미지 ID를 사용하여 S3 URL을 가져오기
            String imageUrl = imageService.getS3UrlByImageId(imageID);

            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image not found. ImageID: " + imageID);
        }
    }

    @GetMapping("/api/manage/image-data/{imageID}")
    public ResponseEntity<String> getImageData(@PathVariable Long imageID) {
        return imageService.getImageJsonById(imageID);
    }

    @DeleteMapping("/api/manage/image/{imageID}")
    public ResponseEntity<String> deleteImage(@PathVariable Long imageID) {
        // 이미지 삭제
        boolean isDeleted = imageService.deleteImage(imageID);

        if (isDeleted) {
            return ResponseEntity.ok("Image delete success");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found or failed to delete");
        }
    }

    @PatchMapping("/api/manage/image/{imageID}/name")
    public ResponseEntity<String> updateImageName(@PathVariable Long imageID, @RequestBody UpdateImageNameRequest request) {
        try {
            imageService.updateImageName(imageID, request.getName());

            return ResponseEntity.ok("Image name update success");
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Image name");
        }
    }
}
