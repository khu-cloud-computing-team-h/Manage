package cloudcomputing.jhs.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;

public class TokenInterceptor implements HandlerInterceptor {

    private static final String NOT_TOKEN_RESPONSE = "{ \"error\": \"토큰을 찾을 수 없습니다.\"}";

    private final String authServerURL;
    private final String authApiURL;

    public TokenInterceptor(@Value("${auth.server-url}") String authServerURL, @Value("${auth.api-url}") String authApiURL) {
        this.authServerURL = authServerURL;
        this.authApiURL = authApiURL;
    }

    // 유저 정보를 담을 record 생성
    public record UserDetail(String id) {}

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Authorization header가 없다면 401 status code 반환
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(NOT_TOKEN_RESPONSE);

            return false;
        }

        try {
            // Auth 서버로부터 유저 ID를 가져옴
            ResponseEntity<UserDetail> authResponse = RestClient.builder()
                    .baseUrl(authServerURL)
                    .defaultHeader(HttpHeaders.AUTHORIZATION, token)
                    .build()
                    .get()
                    .uri(authApiURL)
                    .retrieve()
                    .toEntity(UserDetail.class);

            // Auth 서버로부터 받은 정보를 파싱하고 BigDecimal로 바꿔서 Controller에게 전달함
            request.setAttribute("userId", new BigDecimal(authResponse.getBody().id));

        } catch (HttpClientErrorException errorException) { // 만약 토큰 정보가 이상하면 Exception 발생
            response.setStatus(errorException.getStatusCode().value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(errorException.getResponseBodyAsString());

            return false;
        }

        return true;
    }

}
