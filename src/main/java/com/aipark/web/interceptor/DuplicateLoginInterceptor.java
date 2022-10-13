package com.aipark.web.interceptor;

import com.aipark.biz.domain.member.MemberRepository;
import com.aipark.biz.service.RedisService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Slf4j
public class DuplicateLoginInterceptor implements HandlerInterceptor {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RedisService redisService;

    @Value("${white.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        Map<String, Object> map = objectMapper.readValue(messageBody, new TypeReference<Map<String, Object>>() {
        });
        String username = (String) map.get("username");

        if (redisService.getValues(username) != null) {
            if (redisService.getWhiteValues(username + "_" + secret) == null) {
                return true;
            }
            String whiteValues = redisService.getWhiteValues(username + "_" + secret);
            redisService.setBlackValues(whiteValues, username, Duration.ofMillis(1000 * 60 * 30));
            redisService.deleteWhiteValues(username);
        }
        return true;
    }
}
