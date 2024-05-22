package cloudcomputing.jhs.Image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT COUNT(i) FROM Image i WHERE i.userID = :userID")
    long countByUserID(BigDecimal userID);
}
