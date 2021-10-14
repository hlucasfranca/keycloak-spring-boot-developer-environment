package com.github.thomasdarimont.keycloak.userstorage.flyweight;

import org.jboss.logging.Logger;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.UserModelDelegate;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.federated.UserAttributeFederatedStorage;
import org.keycloak.storage.federated.UserRoleMappingsFederatedStorage;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;

import java.util.*;
import java.util.stream.Collectors;

public class FlyweightAcmeUserStorageProvider implements
        UserStorageProvider
        , UserLookupProvider
        , UserQueryProvider

        , UserRegistrationProvider

        , UserAttributeFederatedStorage

        , CredentialInputUpdater
        , CredentialInputValidator

        , UserRoleMappingsFederatedStorage

{

    private final KeycloakSession session;
    private final ComponentModel storageComponentModel;
    private final AcmeUserRepository repository;
    private final Logger log = Logger.getLogger(getClass());
    protected Map<String, UserModel> loadedUsers = new HashMap<>();

    public FlyweightAcmeUserStorageProvider(KeycloakSession session, ComponentModel storageComponentModel, AcmeUserRepository repository) {
        this.session = session;
        this.storageComponentModel = storageComponentModel;
        this.repository = repository;
    }

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return supportsCredentialType(credentialType);
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {

        log.infov("isValid user credential: userId={0}", user.getId());

        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) input;
        return repository.validateCredentials(user.getUsername(), cred.getValue());
    }

    @Override
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {

        log.infov("updating credential: realm={0} user={1}", realm.getId(), user.getUsername());

        if (!supportsCredentialType(input.getType()) || !(input instanceof UserCredentialModel)) {
            return false;
        }

        UserCredentialModel cred = (UserCredentialModel) input;
        // session.getProvider(PasswordPolicyManagerProvider.class).validate(realm, user, cred.getValue());
        return repository.updateCredentials(user.getUsername(), cred.getValue());
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {
        log.infov("disable credential type: realm={0} user={1} credentialType={2}", realm.getId(), user.getUsername(), credentialType);
    }

    @Override
    public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
        return Collections.emptySet();
    }

    @Override
    public void preRemove(RealmModel realm) {

        log.infov("pre-remove realm");
    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

        log.infov("pre-remove group");
    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

        log.infov("pre-remove role");
    }

    @Override
    public void close() {
        log.infov("closing");
    }

    @Override
    public UserModel getUserById(String id, RealmModel realm) {

        log.infov("lookup user by id: realm={0} userId={1}", realm.getId(), id);

        StorageId userId = new StorageId(id);
        String userName = userId.getExternalId();

        return getUserByUsername(userName, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        UserModel adapter = loadedUsers.get(username);
        if (adapter == null) {
            log.info("not cached");
            UserModel m = createAdapter(realm, username);
            loadedUsers.put(username, m);
            return m;
        }
        log.info("cached");
        return adapter;
    }

    protected UserModel createAdapter(RealmModel realm, String username) {

        UserModel local = session.userLocalStorage().getUserByUsername(username, realm);

        if (local == null) {
            log.info("not in local");

            AcmeUser acmeUser = repository.findUserByUsernameOrEmail(username);

            local = session.userLocalStorage().addUser(realm, username);
            local.setEmail(acmeUser.getEmail());
            local.setEnabled(true);
            local.setEmailVerified(true);
            local.setFirstName(acmeUser.getFirstName());
            local.setLastName(acmeUser.getLastName());

            for(String key : acmeUser.getAttributes().keySet()){
                local.setAttribute(key, acmeUser.getAttributes().get(key));
            }

            local.setFederationLink(storageComponentModel.getId());
        } else {
            log.info("in local");
        }

        return new UserModelDelegate(local);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {

        log.infov("lookup user by username: realm={0} email={1}", realm.getId(), email);

        return getUserByUsername(email, realm);
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return repository.getUsersCount();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm) {

        log.infov("list users: realm={0}", realm.getId());

        return repository.getAllUsers().stream()
                .map(acmeUser -> new AcmeUserAdapter(session, realm, storageComponentModel, acmeUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int firstResult, int maxResults) {

        log.infov("list users: realm={0} firstResult={1} maxResults={2}", realm.getId(), firstResult, maxResults);

        return getUsers(realm);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {

        log.infov("search for users: realm={0} search={1}", realm.getId(), search);

        return searchForUser(search, realm, 0, -1);
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm, int firstResult, int maxResults) {

        log.infov("search for users: realm={0} search={1} firstResult={2} maxResults={3}", realm.getId(), search, firstResult, maxResults);

        if (search.contains(":")) {
            String attributeName = search.substring(0, search.indexOf(":"));
            String attributeValue = search.substring(search.indexOf(":") + 1);

            return repository.findUsersByAttribute(attributeName, attributeValue, firstResult, maxResults).stream()
                    .map(id -> new AcmeUserAdapter(session, realm, storageComponentModel, repository.findUserById(id)))
                    .collect(Collectors.toList());
        }

        return repository.findUsers(search, firstResult, maxResults).stream()
                .map(acmeUser -> new AcmeUserAdapter(session, realm, storageComponentModel, acmeUser))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {

        log.infov("search for users with params: realm={0} params={1}", realm.getId(), params);

        return searchForUser(params, realm, 0, -1);
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm, int firstResult, int maxResults) {

        log.infov("search for users with params: realm={0} params={1} firstResult={2} maxResults={3}", realm.getId(), params, firstResult, maxResults);

        // use params from org.keycloak.models.UserModel

        return searchForUser("", realm);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group) {

        log.infov("search for group members: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId());

        return getGroupMembers(realm, group, 0, -1);
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {

        log.infov("search for group members with params: realm={0} groupId={1} firstResult={2} maxResults={3}", realm.getId(), group.getId(), firstResult, maxResults);

        return Collections.emptyList();
    }

    @Override
    public List<UserModel> searchForUserByUserAttribute(String attrName, String attrValue, RealmModel realm) {

        log.infov("search for user by attribute: realm={0} attrName={1} attrValue={2}", realm.getId(), attrName, attrValue);

        return repository.findUsersByAttribute(attrName, attrValue, 0, -1).stream()
                .map(id -> new AcmeUserAdapter(session, realm, storageComponentModel, repository.findUserById(id)))
                .collect(Collectors.toList());
    }

    /* UserRoleMappingsFederatedStorage start */
    @Override
    public void grantRole(RealmModel realm, String userId, RoleModel role) {
        log.infov("grant role mapping: realm={0} userId={1} role={2}", realm.getId(), userId, role.getName());
    }

    @Override
    public Set<RoleModel> getRoleMappings(RealmModel realm, String userId) {
        log.infov("get role mappings: realm={0} userId={1}", realm.getId(), userId);

        String externalUserId = StorageId.externalId(userId);

        Set<AcmeRole> roles = repository.getGlobalRolesByUserId(externalUserId);
        Set<RoleModel> externalRoles = roles.stream()
                .map(role -> new AcmeRoleModel(role.getId(), role.getName(), role.getDescription(), false, realm))
                .collect(Collectors.toSet());

        for (ClientModel client : realm.getClients()) {

            String clientId = client.getClientId();
            // potentially filter for acme clients...

            Set<AcmeRole> clientRolesByUserId = repository.getClientRolesByUserId(clientId, externalUserId);
            if (clientRolesByUserId != null) {
                Set<RoleModel> externalClientRoles = clientRolesByUserId.stream()
                        .map(role -> new AcmeRoleModel(role.getId(), role.getName(), role.getDescription(), false, client))
                        .collect(Collectors.toSet());
                externalRoles.addAll(externalClientRoles);
            }
        }

        return externalRoles;
    }

    @Override
    public void deleteRoleMapping(RealmModel realm, String userId, RoleModel role) {
        log.infov("delete role mapping: realm={0} userId={1} role={2}", realm.getId(), userId, role.getName());
    }

    /* UserRoleMappingsFederatedStorage end */

    @Override
    public void setSingleAttribute(RealmModel realm, String userId, String name, String value) {
        log.infov("set single attribute: realm={0} userId={1} name={2} value={3}", realm.getId(), userId, name, value);
    }

    @Override
    public void setAttribute(RealmModel realm, String userId, String name, List<String> values) {
        log.infov("set attribute: realm={0} userId={1} name={2} value={3}", realm.getId(), userId, name, values);
    }

    @Override
    public void removeAttribute(RealmModel realm, String userId, String name) {
        log.infov("remove attribute: realm={0} userId={1} name={2}", realm.getId(), userId, name);
    }

    @Override
    public MultivaluedHashMap<String, String> getAttributes(RealmModel realm, String userId) {

        log.infov("get attributes: realm={0} userId={1}", realm.getId(), userId);

        String externalId = StorageId.externalId(userId);
        AcmeUser acmeUser = repository.findUserById(externalId);

        return new MultivaluedHashMap<>(acmeUser.getAttributes());
    }

    @Override
    public List<String> getUsersByUserAttribute(RealmModel realm, String name, String value) {

        log.infov("get users by user attribute: realm={0} name={1} value={2}", realm.getId(), value);

        return repository.findUsersByAttribute(name, value, 0, -1);
    }

//    @Override
//    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
//
//        user.getDelegateForUpdate();
//
//        log.infov("on cache: realm={0} username={1}", realm.getId(), user.getUsername());
//    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {

        log.infov("add user: realm={0} username={1}", realm.getId(), username);

        // this is not supported
        return null;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {

        log.infov("remove user: realm={0} username={1}", realm.getId(), user.getUsername());

        // this is not supported
        return false;
    }

//    @Override
//    public void onCache(RealmModel realm, CachedUserModel user, UserModel delegate) {
//        user.setEmail(delegate.getEmail());
//        user.setUsername(delegate.getUsername());
//
//    }
}
