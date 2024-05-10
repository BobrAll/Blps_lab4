package bobr.mediaMicroservice.image;

import jakarta.persistence.*;
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

    public Base64Image(String base64Image, Integer flatId) {
        this.base64Image = base64Image;
        this.flatId = flatId;
    }
}
