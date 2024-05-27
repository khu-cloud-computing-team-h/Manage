package cloudcomputing.jhs.ImageTag;

import cloudcomputing.jhs.Image.ImageService;
import cloudcomputing.jhs.Tag.Tag;
import cloudcomputing.jhs.Tag.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageTagService {

    @Autowired
    private ImageTagRepository imageTagRepository;

    @Autowired
    private TagService tagService;

    public void deleteAllTagsOfImage(Long imageId) {
        //이미지 ID에 해당하는 모든 이미지 태그를 삭제
        List<ImageTag> imageTags = imageTagRepository.findAllByPkImageID(imageId);

        imageTagRepository.deleteAll(imageTags);
    }

    public void addTagToImage(Long imageId, String tagName) {
        //이미지 태그 엔티티를 생성하여 저장
        ImageTagPK pk = new ImageTagPK();

        pk.setImageID(imageId);

        Tag existingTag = tagService.findByTagName(tagName);
        if (existingTag == null) {
            existingTag = new Tag();
            existingTag.setTagName(tagName);

            existingTag = tagService.saveTag(existingTag);
        }

        pk.setTagID(existingTag.getTagID());

        ImageTag imageTag = new ImageTag();
        imageTag.setPk(pk);

        imageTagRepository.save(imageTag);
    }

    public ImageTag saveImageTag(ImageTag imageTag){
        return imageTagRepository.save(imageTag);
    }
}
