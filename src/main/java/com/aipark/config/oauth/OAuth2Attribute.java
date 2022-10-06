package com.aipark.config.oauth;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private String provider;
    private String attributeKey;
    private String email;
    private String password;
    private String name;

    static OAuth2Attribute of(String provider, String attributeKey, Map<String, Object> attributes) {
        switch (provider) {
            case "google":
                return ofGoogle(attributeKey, attributes);
            case "naver":
                return ofNaver("id", attributes);
            default:
                throw new RuntimeException();
        }
    }

    private static OAuth2Attribute ofGoogle(String attributeKey, Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .provider("google")
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .password((String) attributes.get(attributeKey))
                .attributes(attributes)
                .attributeKey(attributeKey)
                .build();
    }

    private static OAuth2Attribute ofNaver(String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .provider("naver")
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .password((String) response.get(attributeKey))
                .attributes(response)
                .attributeKey(attributeKey)
                .build();
    }

    Map<String, Object> convertToMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("id", attributeKey);
        map.put("key", attributeKey);
        map.put("username", provider + "-" +attributes.get(attributeKey));
        map.put("password", password);
        map.put("name", name);
        map.put("email", email);

        return map;
    }
}
