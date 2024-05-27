package cloudcomputing.jhs.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Tag saveTag(Tag tag){
        return tagRepository.save(tag);
    }

    public Tag findByTagName(String tagName) {
        return tagRepository.findByTagName(tagName);
    }
}
