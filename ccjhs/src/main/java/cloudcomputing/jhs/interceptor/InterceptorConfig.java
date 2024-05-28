package cloudcomputing.jhs.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    @Value("${auth.server-url}")
    private String authServerURL;

    @Value("${auth.api-url}")
    private String authApiURL;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TokenInterceptor(authServerURL, authApiURL))
                .order(1)
                .addPathPatterns("/api/manage/**"); //이미지 업로드에 대한 경로를 지정
    }
}
