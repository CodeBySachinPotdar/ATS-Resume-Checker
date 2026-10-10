package com.resumematch.ai.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AIProviderFactory {

    private final List<AIProvider> providers;

    @Value("${app.ai.provider:mock}")
    private String preferredProvider;

    public AIProviderFactory(List<AIProvider> providers) {
        this.providers = providers;
    }

    public AIProvider getProvider() {
        return providers.stream()
                .filter(p -> p.getProviderName().equalsIgnoreCase(preferredProvider))
                .findFirst()
                .orElseGet(() -> providers.stream()
                        .filter(p -> p.getProviderName().equalsIgnoreCase("mock"))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No AI Provider found!")));
    }
}
