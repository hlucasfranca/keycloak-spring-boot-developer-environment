package com.github.thomasdarimont.keycloak.userstorage.flyweight;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.keycloak.storage.adapter.InMemoryUserAdapter;
import org.keycloak.storage.federated.UserFederatedStorageProvider;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AcmeUserAdapter extends InMemoryUserAdapter {

    private final AcmeUser acmeUser;
    private final String keycloakId;

    public static final String REMOTE_ID = "remoteId";

    public AcmeUserAdapter(KeycloakSession session, RealmModel realm, ComponentModel model, AcmeUser acmeUser) {
        super(session, realm, new StorageId(model.getId(), acmeUser.getUsername()).getId());

        setSingleAttribute(REMOTE_ID, acmeUser.getId());
        setUsername(acmeUser.getUsername());
        setFirstName(acmeUser.getFirstName());
        setLastName(acmeUser.getLastName());
        setEmail(acmeUser.getEmail());
        setEnabled(acmeUser.isEnabled());
        setEmailVerified(true);
        acmeUser.getAttributes().keySet().forEach(k -> {
            setAttribute(k, acmeUser.getAttributes().get(k));
        });


        addDefaults();

        this.acmeUser = acmeUser;
        this.keycloakId = StorageId.keycloakId(model, acmeUser.getId());
    }

}
