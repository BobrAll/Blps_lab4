package bobr.mainMicroservice.image;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Base64ImageService {

    private final ImageRepository imageRepository;

    public static byte[] toByteImage(Base64Image img) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(img.getBase64Image());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);

            return out.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Base64Image> findAllByFlatId(Integer flatId) {
        return imageRepository.findAllByFlatId(flatId);
    }

}
