package bobr.mainMicroservice.realty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Realty {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(nullable = false)
    private Double totalArea;
    @Column(nullable = false)
    private Double totalPrice;
    @Column(nullable = false)
    private String address;

    public Double pricePerMeter() {
        return totalPrice / totalArea;
    }
}
