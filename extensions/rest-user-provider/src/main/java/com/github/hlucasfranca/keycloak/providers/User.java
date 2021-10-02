package com.github.hlucasfranca.keycloak.providers;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private String firstname;
    private String lastname;
    private String email;
    private String userName;
    private String userId;

}
