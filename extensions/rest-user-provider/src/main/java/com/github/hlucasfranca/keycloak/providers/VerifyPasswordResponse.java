package com.github.hlucasfranca.keycloak.providers;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyPasswordResponse {

    private boolean result;

}
