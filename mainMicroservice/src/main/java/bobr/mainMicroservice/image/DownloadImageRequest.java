package bobr.mainMicroservice.image;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadImageRequest {
    private String url;
    private Integer flatId;
}
