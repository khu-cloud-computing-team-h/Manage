package cloudcomputing.jhs.ImageTag;

import cloudcomputing.jhs.Image.ImageService;
import cloudcomputing.jhs.Tag.Tag;
import cloudcomputing.jhs.Tag.TagService;
import cloudcomputing.jhs.UploadImageTagsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class ImageTagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private ImageTagService imageTagService;

    @Autowired
    private ImageService imageService;


    @PostMapping("/api/manage/image/upload/tags")
    public ResponseEntity<String> uploadImageTags(@RequestBody UploadImageTagsRequest request){
        try {
            Long imageId = request.getImageId();
            List<String> tags = request.getTags();

            //이미지 ID의 존재 여부 확인
            if (!imageService.existsById(imageId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image ID not found." + "Image ID = "+ imageId);
            }

            for(String tagName : tags){
                //이미 존재하는 태그인지 확인
                Tag existingTag = tagService.findByTagName(tagName);

                //이미 존재하는 태그가 없는 경우 새로운 태그 생성
                if (existingTag == null) {
                    existingTag = new Tag();
                    existingTag.setTagName(tagName);

                    existingTag = tagService.saveTag(existingTag); //저장된 태그를 다시 가져옴
                }

                //ImageTag 엔티티에 이미지 아이디와 태그 아이디를 저장
                ImageTag imageTag = new ImageTag(existingTag.getTagID(), imageId);
                imageTagService.saveImageTag(imageTag);
            }

            return ResponseEntity.ok("Image tags uploade success");
        } catch (Exception e){
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload Image tags");
        }
    }

    @PatchMapping("/api/manage/image/{imageId}/tags")
    public ResponseEntity<String> updateImageTags(@PathVariable Long imageId, @RequestBody List<String> tags) {
        try {
            //이미지가 존재하는지 확인
            if (!imageService.existsById(imageId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Image not found. ImageID: " + imageId);
            }

            //이미지의 모든 태그 삭제
            imageTagService.deleteAllTagsOfImage(imageId);

            //새로운 태그 추가
            for (String tagName : tags) {
                imageTagService.addTagToImage(imageId, tagName);
            }

            return ResponseEntity.ok("Image tags update success");
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update Image tags");
        }
    }
}
