package bobr.mainMicroservice.image;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Base64Image {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer flatId;

    @Column(columnDefinition = "TEXT")
    private String base64Image;
}
