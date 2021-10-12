package com.github.thomasdarimont.keycloak.userstorage.flyweight;

import org.keycloak.models.ClientModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class AcmeRoleModel implements RoleModel {

    private String id;

    private String name;

    private String description;

    private boolean composite;

    private RoleContainerModel roleContainer;

    public AcmeRoleModel(String id, String name, String description, boolean composite, RoleContainerModel roleContainer) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.composite = composite;
        this.roleContainer = roleContainer;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public RoleContainerModel getRoleContainer() {
        return roleContainer;
    }

    public void setRoleContainer(RoleContainerModel roleContainer) {
        this.roleContainer = roleContainer;
    }

    @Override
    public void addCompositeRole(RoleModel role) {
        throw new UnsupportedOperationException("addCompositeRole");
    }

    @Override
    public void removeCompositeRole(RoleModel role) {
        throw new UnsupportedOperationException("removeCompositeRole");
    }

    @Override
    public Stream<RoleModel> getCompositesStream() {
        return Stream.empty();
    }

    @Override
    public boolean isClientRole() {
        return roleContainer instanceof ClientModel;
    }

    @Override
    public String getContainerId() {
        return roleContainer.getId();
    }

    @Override
    public RoleContainerModel getContainer() {
        return roleContainer;
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return false;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        throw new UnsupportedOperationException("setSingleAttribute");
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        throw new UnsupportedOperationException("setAttribute");
    }

    @Override
    public void removeAttribute(String name) {
        throw new UnsupportedOperationException("removeAttribute");
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return Stream.empty();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        return Collections.emptyMap();
    }
}
