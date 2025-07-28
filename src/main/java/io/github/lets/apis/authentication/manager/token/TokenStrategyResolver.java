package io.github.lets.apis.authentication.manager.token;

import io.github.lets.apis.authentication.enums.TokenType;
import io.github.lets.apis.authentication.manager.token.base.TokenManager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TokenStrategyResolver {

    private final Map<TokenType, TokenManager> strategyMap = new EnumMap<>(TokenType.class);

    public TokenStrategyResolver(List<TokenManager> strategies) {
        for (TokenManager strategy : strategies) {
            strategyMap.put(strategy.type(), strategy);
        }
    }

    public TokenManager resolve(TokenType tokenType) {
        TokenManager strategy = strategyMap.get(tokenType);
        if (strategy == null) {
            throw new IllegalArgumentException("Token type not supported: " + tokenType);
        }
        return strategy;
    }
}