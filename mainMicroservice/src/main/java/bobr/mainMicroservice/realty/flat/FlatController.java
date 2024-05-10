package bobr.mainMicroservice.realty.flat;

import bobr.mainMicroservice.image.Base64ImageService;
import bobr.mainMicroservice.user.User;
import bobr.mainMicroservice.user.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Flat")
@RequestMapping("/api/v1/flats")
public class FlatController {

    private static final int GAP_FROM_HUMAN_READABLE_NUM_TO_IDX = 1;

    private final UserService userService;
    private final FlatService flatService;
    private final Base64ImageService imageService;

    @GetMapping
    public List<Flat> getFlats(@RequestBody FlatCriteria filters) {
        return flatService.findAllByFilters(filters);
    }

    @PostMapping()
    @Transactional
    public void add(@RequestBody FlatAddRequest flatRequest) {
        User user = userService.getCurrentUser();

        flatRequest.setOwnerId(user.getId());
        flatService.save(flatRequest);

        if (flatRequest.getIsBoosted() &&
                !user.getAuthorities().stream().anyMatch(a -> a.toString().equals("flat.boost"))) {

            userService.buySubscription(user);
        }
    }

    @GetMapping("/{flatId}")
    public Flat getFlat(@PathVariable Integer flatId) {
        return flatService.findById(flatId);
    }

    @DeleteMapping("/{flatId}")
    public ResponseEntity removeFlat(@PathVariable Integer flatId) {
        User currentUser = userService.getCurrentUser();

        if (
                flatService
                        .findOwnerIdByFlatId(flatId)
                        .equals(currentUser.getId()) ||
                        currentUser
                                .getAuthorities()
                                .contains("flat.delete")
        ) {
            flatService.delete(flatId);
            return ResponseEntity
                    .ok()
                    .build();
        } else
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
    }

    @PutMapping("/{flatId}/boost")
    public void boostFlat(@PathVariable Integer flatId) {
        flatService.boost(flatId);
    }

    @GetMapping("/{flatId}/images/{imageId}")
    public ResponseEntity<byte[]> getFlatImage(
            @PathVariable Integer flatId,
            @PathVariable Integer imageId) {

        List<byte[]> byteImages = imageService.findAllByFlatId(flatId)
                .stream()
                .map(Base64ImageService::toByteImage)
                .toList();

        try {
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(byteImages.get(imageId - GAP_FROM_HUMAN_READABLE_NUM_TO_IDX));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

}
