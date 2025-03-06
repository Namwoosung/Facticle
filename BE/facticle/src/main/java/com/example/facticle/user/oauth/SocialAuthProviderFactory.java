package com.example.facticle.user.oauth;

import com.example.facticle.common.exception.InvalidInputException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocialAuthProviderFactory {
    private final List<SocialAuthProvider> providers;

    public SocialAuthProvider getAuthProvider(String provider){
        return providers.stream()
                .filter(p -> p.support(provider))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Invalid input", Map.of("provider", "cannot find OAuth Provider")));
    }

}
