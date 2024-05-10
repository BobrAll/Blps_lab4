package bobr.mediaMicroservice.realty.flat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FlatRepository extends JpaRepository<Flat, Integer> {}
