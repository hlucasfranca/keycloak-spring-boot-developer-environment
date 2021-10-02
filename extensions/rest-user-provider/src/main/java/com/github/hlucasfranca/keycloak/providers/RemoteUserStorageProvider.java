package com.github.hlucasfranca.keycloak.providers;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

@JBossLog
public class RemoteUserStorageProvider implements UserStorageProvider, UserLookupProvider, CredentialInputValidator {

    private KeycloakSession session;
    private ComponentModel model;
    private UserApiService userApiService;

    public RemoteUserStorageProvider(KeycloakSession session, ComponentModel model, UserApiService userApiService) {
        this.session = session;
        this.model = model;
        this.userApiService = userApiService;
    }

    @Override
    public void close() {

    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        log.info("getUserById local");
        return UserLookupProvider.super.getUserById(realm, id);
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        log.info("getUserById local");
        return null;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        log.info("getUserByUsername local");
        return UserLookupProvider.super.getUserByUsername(realm, username);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        log.info("getUserByUsername rest");
        UserModel userModel = null;
        User user = userApiService.getUserDetails(username);

        if(user != null){
            userModel = createUserModel(username, realm);
        }

        return userModel;
    }

    private UserModel createUserModel(String username, RealmModel realm) {
        log.info("createUserModel");
        return new AbstractUserAdapter(session, realm, model){
            @Override
            public String getUsername(){
                return username;
            }
        };
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        log.info("getUserByEmail");
        return UserLookupProvider.super.getUserByEmail(realm, email);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        log.info("getUserByEmail");
        return null;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {

        if(!supportsCredentialType(credentialType)) return false;
        return !getCredentialStore().getStoredCredentialsByType(realm, user, credentialType).isEmpty();
    }

    private UserCredentialStore getCredentialStore() {
        return session.userCredentialManager();
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("getUserByEmail");

        VerifyPasswordResponse verifyPasswordResponse = userApiService.verifyUserPassword(user.getUsername(),
                credentialInput.getChallengeResponse());
        if(verifyPasswordResponse == null) return false;

        return verifyPasswordResponse.isResult();
    }
}
