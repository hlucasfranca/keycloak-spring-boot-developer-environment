package com.github.hlucasfranca.keycloak.providers;

public class UserApiServiceMock implements UserApiService{
    @Override
    public User getUserDetails(String username) {
        return User.builder()
                .userName(username)
                .userId("1")
                .firstname("nome")
                .lastname("nome")
                .build();
    }

    @Override
    public VerifyPasswordResponse verifyUserPassword(String username, String password) {
        return VerifyPasswordResponse.builder().result(true).build();
    }
}
