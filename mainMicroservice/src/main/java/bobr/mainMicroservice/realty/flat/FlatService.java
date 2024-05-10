package bobr.mainMicroservice.realty.flat;

import bobr.mainMicroservice.config.MqttConfig;
import bobr.mainMicroservice.exceptions.flat.NoSuchFlatException;
import bobr.mainMicroservice.image.Base64ImageService;
import bobr.mainMicroservice.image.DownloadImageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class FlatService {

    private final MqttConfig.MQTTGateway mqttGateway;
    private final Base64ImageService imageService;
    private final FlatRepository flatRepository;

    public Flat findById(Integer flatId) {
        return flatRepository
                .findById(flatId)
                .orElseThrow(() -> new NoSuchFlatException(flatId));
    }

    public List<Flat> findAllByFilters(FlatCriteria searchFilters) {
        return flatRepository.findWithFilters(
                searchFilters.getMinTotalArea(),
                searchFilters.getMaxTotalArea(),
                searchFilters.getMinKitchenArea(),
                searchFilters.getMaxKitchenArea(),
                searchFilters.getMinLivingArea(),
                searchFilters.getMaxLivingArea(),
                searchFilters.getRooms(),
                searchFilters.getFloor(),
                searchFilters.getHaveBalcony(),
                searchFilters.getIsApartments()
                );
    }

    public void save(Flat flat) {
        flatRepository.save(flat);
    }

    public void delete(Integer flatId) {
        flatRepository.delete(findById(flatId));
    }

    public void boost(Integer flatId) {
        Flat flat = findById(flatId);

        flat.setIsBoosted(true);
        save(flat);
    }


    public void unboostAllFlats(Integer userId) {
        List<Flat> flats = flatRepository.findAllByOwnerId(userId);

        flats.forEach(flat -> flat.setIsBoosted(false));
        flatRepository.saveAll(flats);
    }

    public Integer findOwnerIdByFlatId(Integer flatId) {
        return flatRepository.findOwnerIdByFlatId(flatId);
    }

    public Flat save(FlatAddRequest flatRequest) {
        ObjectMapper jsonMapper = new ObjectMapper();

        Flat flat = Flat.builder()
                .totalArea(flatRequest.getTotalArea())
                .totalPrice(flatRequest.getTotalPrice())
                .address(flatRequest.getAddress())
                .ownerId(flatRequest.getOwnerId())
                .isBoosted(flatRequest.getIsBoosted())
                .haveBalcony(flatRequest.getHaveBalcony())
                .isApartments(flatRequest.getIsApartments())
                .kitchenArea(flatRequest.getKitchenArea())
                .livingArea(flatRequest.getLivingArea())
                .rooms(flatRequest.getRooms())
                .floor(flatRequest.getFloor())
                .build();

        flat = flatRepository.save(flat);

        if (flatRequest.getImageUrls() != null) {
            Flat finalFlat = flat;
            flatRequest.getImageUrls()
                    .forEach(url -> {
                                try {
                                    mqttGateway.sendToMqtt(jsonMapper.writeValueAsString(new DownloadImageRequest(url, finalFlat.getId())));
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
        }
        return flat;
    }

}
