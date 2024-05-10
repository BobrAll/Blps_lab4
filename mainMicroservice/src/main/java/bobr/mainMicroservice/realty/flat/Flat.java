package bobr.mainMicroservice.realty.flat;

import bobr.mainMicroservice.realty.Realty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Flat extends Realty {

    @Column(nullable = false)
    private Integer ownerId;
    private Boolean isBoosted;
    private Boolean haveBalcony;
    private Boolean isApartments;
    private Double kitchenArea;
    private Double livingArea;

    @Column(nullable = false)
    private Integer rooms;

    @Column(nullable = false)
    private Integer floor;

}
