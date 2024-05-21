package cloudcomputing.jhs.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT COUNT(i) FROM Image i WHERE i.userID = :userID")
    long countByUserID(String userID);
}
