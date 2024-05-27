package cloudcomputing.jhs.ImageTag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImageTagService {

    @Autowired
    private ImageTagRepository imageTagRepository;

    public ImageTag saveImageTag(ImageTag imageTag){
        return imageTagRepository.save(imageTag);
    }
}
