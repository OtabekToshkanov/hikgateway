package uz.datalab.verifix.hikgateway.client.vhr.entity.load;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SetPhotoCommandBody {
    @JsonProperty("FaceDataRecord")
    private FaceDataRecord faceDataRecord;
    @JsonProperty("FaceImage")
    private FaceImage faceImage;
}
