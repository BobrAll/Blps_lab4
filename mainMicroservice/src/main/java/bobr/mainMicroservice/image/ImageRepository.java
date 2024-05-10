package bobr.mainMicroservice.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Base64Image, Integer> {

    @Query("SELECT img FROM Base64Image img WHERE img.flatId = :flatId")
    List<Base64Image> findAllByFlatId(Integer flatId);

}
