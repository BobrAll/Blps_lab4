package bobr.mediaMicroservice.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Base64Image, Integer> {}
