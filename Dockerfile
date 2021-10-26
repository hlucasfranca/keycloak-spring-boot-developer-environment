FROM jboss/keycloak:15.0.2

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin

ENV KEYCLOAK_IMPORT /tmp/import_realm.json
COPY imports/realm.json /tmp/import_realm.json

COPY ./themes/ /opt/jboss/keycloak/themes/
COPY ./deployments /opt/jboss/keycloak/standalone/deployments

EXPOSE 8080