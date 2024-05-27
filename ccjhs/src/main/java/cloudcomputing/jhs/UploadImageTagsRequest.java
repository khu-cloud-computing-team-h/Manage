package cloudcomputing.jhs;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UploadImageTagsRequest {

    private Long imageId;
    private List<String> tags;
}
