package cloudcomputing.jhs.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        //파일명을 URL 디코딩하여 사용
        String fileName = decodeFileName(file.getOriginalFilename());

        //이하 업로드 로직은 그대로 유지
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    //S3에 업로드된 이미지 파일을 삭제하는 메서드
    public void deleteS3Image(String fileUrl) {
        //S3 URL에서 키 추출
        String decodedKey = decodeKey(fileUrl);

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, decodedKey));
    }

    //URL 디코딩된 파일명 반환 메서드
    private String decodeFileName(String fileName) {
        try {
            return URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            //디코딩 실패 시 기본 파일명 반환
            return fileName;
        }
    }

    //S3 URL에서 키 디코딩하여 반환하는 메서드
    private String decodeKey(String fileUrl) {
        //URL에서 마지막 / 다음 문자열이 키 값이므로, 해당 부분만 추출 후 디코딩
        String[] parts = fileUrl.split("/");
        String key = parts[parts.length - 1];

        try {
            return URLDecoder.decode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            //디코딩 실패 시 기본 키 값 반환
            return key;
        }
    }
}