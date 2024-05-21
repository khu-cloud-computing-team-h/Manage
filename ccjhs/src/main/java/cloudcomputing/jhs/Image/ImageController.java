package cloudcomputing.jhs.Image;

import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/api/manage/image")
    public ResponseEntity<String> uploadImage(@RequestParam("userID") String userID, @RequestParam("imageFile")MultipartFile imageFile){

        //이미지를 저장할 디렉토리 경로 설정
        //s3url로 변경 예정
        String uploadDir = "C:\\Users\\jhs\\Desktop\\경희대학교\\2024-1학기\\클라우드컴퓨팅\\ccProject\\images\\" + userID + "/";

        File directory = new File(uploadDir);

        if(!directory.exists()){
            directory.mkdirs();
        }

        //이미지 파일의 원본 파일 이름
        String originalFileName = imageFile.getOriginalFilename();

        //이미지 파일 확장자 추출
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        try{
            //이미지 ID 생성
            String imageID = userID + "_" + System.currentTimeMillis(); //현재 시간을 사용하여 고유한 ID 생성

            // 파일명은 이미지 ID + 확장자로 설정
            String fileName = imageID + fileExtension;

            //이미지 저장할 경로 설정
            Path uploadPath = Paths.get(uploadDir+fileName);

            //이미지 디스크에 저장
            Files.write(uploadPath, imageFile.getBytes());

            //MySQL에 저장
            imageService.saveImage(imageID, userID, uploadPath.toString());

        } catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload");
        }

        return ResponseEntity.ok("Image upload success");
    }
}
