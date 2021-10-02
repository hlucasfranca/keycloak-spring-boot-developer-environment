FROM jboss/keycloak:15.0.2

COPY ./themes/ /opt/jboss/keycloak/themes/
COPY ./deployments /opt/jboss/keycloak/standalone/deployments

ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin

EXPOSE 8080