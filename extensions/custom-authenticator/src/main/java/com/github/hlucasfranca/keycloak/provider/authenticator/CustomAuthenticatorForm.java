package com.github.hlucasfranca.keycloak.provider.authenticator;

import lombok.extern.jbosslog.JBossLog;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.events.Errors;
import org.keycloak.events.EventBuilder;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.services.messages.Messages;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if("aa".equals("aa"))
            throw new IllegalArgumentException("erro");

        EventBuilder event = context.getEvent();
        event.detail("customDetail", "my custom detail");

        UserModel user = session.users().getUserByUsername(context.getRealm(), context.getHttpRequest().getDecodedFormParameters().getFirst("username"));

        if(!user.isEnabled()){
            log.infov("{0}", event.getEvent().getDetails());
            event.error("meu_erro_personalizado");

            List<FormMessage> lista = new ArrayList<>();
            lista.add(new FormMessage("Conta desativada"));
            lista.add(new FormMessage("Sistema fora do ar"));
            LoginFormsProvider loginFormsProvider = context.form().setErrors(lista);

//            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, loginFormsProvider.createForm("custom-form.ftl"));
            context.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, loginFormsProvider.createForm("generic-error.ftl"));
        } else {
            context.setUser(user);
            context.getUser().getAttributes().put("teste-put", Arrays.asList("teste.put"));
            log.infov("{0}", event.getEvent().getDetails());
            context.success();
        }

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
