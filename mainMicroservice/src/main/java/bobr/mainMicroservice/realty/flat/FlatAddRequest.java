package bobr.mainMicroservice.realty.flat;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FlatAddRequest {

    private Double totalArea;
    private Double totalPrice;

    private String address;
    private Set<String> imageUrls;
    private Integer ownerId;

    private Boolean isBoosted;
    private Boolean haveBalcony;
    private Boolean isApartments;

    private Double kitchenArea;
    private Double livingArea;

    private Integer rooms;
    private Integer floor;

}
