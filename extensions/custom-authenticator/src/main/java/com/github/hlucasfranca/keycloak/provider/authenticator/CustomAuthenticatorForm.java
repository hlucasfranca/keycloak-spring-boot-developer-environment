package com.github.hlucasfranca.keycloak.provider.authenticator;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.ws.rs.core.Response;

@JBossLog
public class CustomAuthenticatorForm implements Authenticator {

    static final String ID = "custom-authenticator";

    private final KeycloakSession session;

    public CustomAuthenticatorForm(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        Response response = context.form()
                .createForm("custom-form.ftl");
        context.challenge(response);
    }

    @Override
    public void action(AuthenticationFlowContext context) {

        UserModel demo = session.users().getUserByUsername(context.getRealm(), "demo");

        if(!demo.isEnabled()){
            System.out.println("oi");
        } else {
            System.out.println("oi");
        }

        //context.challenge(context.form().createRegistration());
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
        // NOOP
    }
}
