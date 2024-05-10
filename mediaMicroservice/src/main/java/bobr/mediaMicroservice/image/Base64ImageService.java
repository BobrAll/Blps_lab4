package bobr.mediaMicroservice.image;

import bobr.mediaMicroservice.realty.flat.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class Base64ImageService {

    private final ImageRepository imageRepository;
    private final FlatRepository flatRepository;

    public static Base64Image download(String url, Integer flatId) {
        byte[] imageBytes = new byte[0];
        String imgExtension = url.substring(url.length() - 3);

        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            ImageIO.write(image, imgExtension, out);
            imageBytes = out.toByteArray();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return new Base64Image(base64Image, flatId);
    }

    public void save(Base64Image image) {
        if (flatRepository.findById(image.getFlatId()).isPresent())
            imageRepository.save(image);
    }

}
