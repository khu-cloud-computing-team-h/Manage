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

        //파일명은 userID로 설정
        String fileName = userID + "_" + imageFile.getOriginalFilename();

        //이미지를 저장할 경로 설정
        Path uploadPath = Paths.get(uploadDir + fileName);

        try{
            //이미지 디스크에 저장
            Files.write(uploadPath, imageFile.getBytes());
        } catch (IOException e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload");
        }

        return ResponseEntity.ok("Image upload success");
    }
}
