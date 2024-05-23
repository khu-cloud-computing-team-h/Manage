package cloudcomputing.jhs.Image;

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

    @PostMapping("/api/manage/image")
    public ResponseEntity<String> uploadImage(@RequestParam("userID") String userID, @RequestParam("imageFile") MultipartFile imageFile) {

        //이미지를 저장할 디렉토리 경로 설정
        String uploadDir = "C:\\Users\\jhs\\Desktop\\경희대학교\\2024-1학기\\클라우드컴퓨팅\\CloudComputing-project\\images\\" + userID + "/";
        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            //이미지 파일의 원본 파일 이름
            String originalFileName = imageFile.getOriginalFilename();
            //이미지 파일의 확장자 추출
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

            //이미지 ID 생성(현재 시간을 사용하여 고유한 ID 생성)
            Long imageID = System.currentTimeMillis();
            //파일명은 이미지 ID + 확장자로 설정
            String fileName = imageID + fileExtension;

            //이미지를 저장할 경로 설정
            Path uploadPath = Paths.get(uploadDir + fileName);
            //이미지 디스크에 저장
            Files.write(uploadPath, imageFile.getBytes());

            //MySQL에 이미지 정보 저장
            BigDecimal userIdBigDecimal = new BigDecimal(userID);
            imageService.saveImage(imageID, userIdBigDecimal, uploadPath.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Image upload Fail");
        }
        return ResponseEntity.ok("Image upload success");
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
}
