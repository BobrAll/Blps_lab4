package bobr.mainMicroservice.realty.flat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FlatRepository extends JpaRepository<Flat, Integer> {

    @Query("SELECT f FROM Flat f " +
            "WHERE (:floor IS NULL OR f.floor = :floor) " +
            "AND (:rooms IS NULL OR f.rooms IN :rooms) " +
            "AND (:haveBalcony IS NULL OR f.haveBalcony = :haveBalcony) " +
            "AND (:isApartments IS NULL OR f.isApartments = :isApartments) " +
            "AND (:minTotalArea IS NULL OR f.totalArea >= :minTotalArea) " +
            "AND (:maxTotalArea IS NULL OR f.totalArea <= :maxTotalArea) " +
            "AND (:minKitchenArea IS NULL OR f.kitchenArea >= :minKitchenArea) " +
            "AND (:maxKitchenArea IS NULL OR f.kitchenArea <= :maxKitchenArea) " +
            "AND (:minLivingArea IS NULL OR f.livingArea >= :minLivingArea) " +
            "AND (:maxLivingArea IS NULL OR f.livingArea <= :maxLivingArea)"
    )
    List<Flat> findWithFilters(
            Double minTotalArea,
            Double maxTotalArea,
            Double minKitchenArea,
            Double maxKitchenArea,
            Double minLivingArea,
            Double maxLivingArea,
            Integer[] rooms,
            Integer floor,
            Boolean haveBalcony,
            Boolean isApartments
            );

    List<Flat> findAllByOwnerId(Integer ownerId);

    @Query("SELECT f.ownerId FROM Flat f WHERE f.id = :flatId")
    Integer findOwnerIdByFlatId(Integer flatId);

}
