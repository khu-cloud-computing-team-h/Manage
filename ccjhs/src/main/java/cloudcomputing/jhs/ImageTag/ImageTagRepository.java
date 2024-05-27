package cloudcomputing.jhs.ImageTag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageTagRepository extends JpaRepository<ImageTag, ImageTagPK> {
    List<ImageTag> findAllByPkImageID(Long imageId);
}
