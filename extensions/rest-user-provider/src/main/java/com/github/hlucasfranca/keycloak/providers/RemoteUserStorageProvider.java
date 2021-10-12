package com.github.hlucasfranca.keycloak.providers;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.credential.UserCredentialStore;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.federated.UserFederatedStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

import java.util.List;
import java.util.Set;

@JBossLog
public class RemoteUserStorageProvider implements UserFederatedStorageProvider, UserLookupProvider, CredentialInputValidator, UserStorageProvider {

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
        return new AbstractUserAdapterFederatedStorage(session, realm, model){
            @Override
            public String getUsername(){
                return username;
            }

            @Override
            public void setUsername(String username) {

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

    @Override
    public List<String> getStoredUsers(RealmModel realm, int first, int max) {
        return null;
    }

    @Override
    public int getStoredUsersCount(RealmModel realm) {
        return 0;
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Override
    public void preRemove(RealmModel realm, ClientModel client) {

    }

    @Override
    public void preRemove(ProtocolMapperModel protocolMapper) {

    }

    @Override
    public void preRemove(ClientScopeModel clientScope) {

    }

    @Override
    public void preRemove(RealmModel realm, UserModel user) {

    }

    @Override
    public void preRemove(RealmModel realm, ComponentModel model) {

    }

    @Override
    public void setSingleAttribute(RealmModel realm, String userId, String name, String value) {

    }

    @Override
    public void setAttribute(RealmModel realm, String userId, String name, List<String> values) {

    }

    @Override
    public void removeAttribute(RealmModel realm, String userId, String name) {

    }

    @Override
    public MultivaluedHashMap<String, String> getAttributes(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public List<String> getUsersByUserAttribute(RealmModel realm, String name, String value) {
        return null;
    }

    @Override
    public String getUserByFederatedIdentity(FederatedIdentityModel socialLink, RealmModel realm) {
        return null;
    }

    @Override
    public void addFederatedIdentity(RealmModel realm, String userId, FederatedIdentityModel socialLink) {

    }

    @Override
    public boolean removeFederatedIdentity(RealmModel realm, String userId, String socialProvider) {
        return false;
    }

    @Override
    public void preRemove(RealmModel realm, IdentityProviderModel provider) {

    }

    @Override
    public void updateFederatedIdentity(RealmModel realm, String userId, FederatedIdentityModel federatedIdentityModel) {

    }

    @Override
    public Set<FederatedIdentityModel> getFederatedIdentities(String userId, RealmModel realm) {
        return null;
    }

    @Override
    public FederatedIdentityModel getFederatedIdentity(String userId, String socialProvider, RealmModel realm) {
        return null;
    }

    @Override
    public void addConsent(RealmModel realm, String userId, UserConsentModel consent) {

    }

    @Override
    public UserConsentModel getConsentByClient(RealmModel realm, String userId, String clientInternalId) {
        return null;
    }

    @Override
    public List<UserConsentModel> getConsents(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void updateConsent(RealmModel realm, String userId, UserConsentModel consent) {

    }

    @Override
    public boolean revokeConsentForClient(RealmModel realm, String userId, String clientInternalId) {
        return false;
    }

    @Override
    public void updateCredential(RealmModel realm, String userId, CredentialModel cred) {

    }

    @Override
    public CredentialModel createCredential(RealmModel realm, String userId, CredentialModel cred) {
        return null;
    }

    @Override
    public boolean removeStoredCredential(RealmModel realm, String userId, String id) {
        return false;
    }

    @Override
    public CredentialModel getStoredCredentialById(RealmModel realm, String userId, String id) {
        return null;
    }

    @Override
    public List<CredentialModel> getStoredCredentials(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public List<CredentialModel> getStoredCredentialsByType(RealmModel realm, String userId, String type) {
        return null;
    }

    @Override
    public CredentialModel getStoredCredentialByNameAndType(RealmModel realm, String userId, String name, String type) {
        return null;
    }

    @Override
    public Set<GroupModel> getGroups(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void joinGroup(RealmModel realm, String userId, GroupModel group) {

    }

    @Override
    public void leaveGroup(RealmModel realm, String userId, GroupModel group) {

    }

    @Override
    public List<String> getMembership(RealmModel realm, GroupModel group, int firstResult, int max) {
        return null;
    }

    @Override
    public void setNotBeforeForUser(RealmModel realm, String userId, int notBefore) {

    }

    @Override
    public int getNotBeforeOfUser(RealmModel realm, String userId) {
        return 0;
    }

    @Override
    public Set<String> getRequiredActions(RealmModel realm, String userId) {
        return null;
    }

    @Override
    public void addRequiredAction(RealmModel realm, String userId, String action) {

    }

    @Override
    public void removeRequiredAction(RealmModel realm, String userId, String action) {

    }

    @Override
    public Set<RoleModel> getRoleMappings(RealmModel realm, String userId) {
        log.info("getRoleMappings");
        return null;
    }

    @Override
    public void grantRole(RealmModel realm, String userId, RoleModel role) {

    }

    @Override
    public void deleteRoleMapping(RealmModel realm, String userId, RoleModel role) {

    }
}
