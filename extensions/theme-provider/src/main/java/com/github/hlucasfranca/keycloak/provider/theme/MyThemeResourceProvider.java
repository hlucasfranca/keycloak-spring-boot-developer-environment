package com.github.hlucasfranca.keycloak.provider.theme;

import org.keycloak.models.KeycloakSession;
import org.keycloak.theme.ThemeResourceProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class MyThemeResourceProvider implements ThemeResourceProvider {

    private KeycloakSession session;

    public MyThemeResourceProvider(KeycloakSession session) {
        this.session  = session;
    }

    @Override
    public URL getTemplate(String s) throws IOException {
        return getClass().getClassLoader().getResource("theme-resources/templates/" + s);
    }

    @Override
    public InputStream getResourceAsStream(String s) throws IOException {
        return null;
    }

    @Override
    public void close() {

    }
}
