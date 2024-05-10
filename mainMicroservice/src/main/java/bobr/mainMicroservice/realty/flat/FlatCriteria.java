package bobr.mainMicroservice.realty.flat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatCriteria {
    private Double minTotalArea;
    private Double maxTotalArea;
    private Double minKitchenArea;
    private Double maxKitchenArea;
    private Double minLivingArea;
    private Double maxLivingArea;
    private Integer[] rooms;
    private Integer floor;
    private Boolean haveBalcony;
    private Boolean isApartments;
}
