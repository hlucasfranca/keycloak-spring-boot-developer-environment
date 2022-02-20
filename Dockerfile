ARG my_arg

FROM jboss/keycloak:15.0.2 as base

RUN echo "Copiando defaults"
COPY ./themes/ /opt/jboss/keycloak/themes/

FROM base AS ambiente-dev
ENV AMBIENTE=DESENVOLVIMENTO
RUN echo "Ambiente de desenvolvimento"
USER root
RUN sed -i 's/^parent=.*/parent=oi/g' /opt/jboss/keycloak/themes/custom-theme/login/theme.properties

FROM base AS ambiente-hml
ENV AMBIENTE=HOMOLOGACAO
RUN echo "Ambiente de homologação"
USER root
RUN sed -i 's/^parent=.*/parent=tchau/g' /opt/jboss/keycloak/themes/custom-theme/login/theme.properties

FROM ambiente-${my_arg} AS final
RUN echo "Ambiente é ${AMBIENTE}"

# ENV KEYCLOAK_LOGLEVEL=DEBUG
ENV KEYCLOAK_USER=admin
ENV KEYCLOAK_PASSWORD=admin

# Global configuration for Keycloak environment
ENV KEYCLOAK_VERSION=15.0.2
# ENV KEYCLOAK_FRONTEND_URL=https://id.acme.test:1443/auth
ENV KEYCLOAK_REMOTE_ISPN_HOSTNAME1=infinispan
ENV KEYCLOAK_REMOTE_ISPN_HOSTNAME2=acme-ispn-2
ENV KEYCLOAK_REMOTE_ISPN_USERNAME=keycloak
ENV KEYCLOAK_REMOTE_ISPN_PASSWORD=password
ENV KEYCLOAK_REMOTE_ISPN_TRUSTSTORE_PASSWORD=password
ENV KEYCLOAK_REMOTE_ISPN_SOCK_TIMEOUT=60000
ENV KEYCLOAK_REMOTE_ISPN_CONN_TIMEOUT=5000


# ENV KEYCLOAK_IMPORT /tmp/import_realm.json
# COPY imports/realm.json /tmp/import_realm.json

RUN mkdir -p /opt/jboss/startup-scripts/
COPY ./startup_scripts /opt/jboss/startup-scripts
COPY ./themes/ /opt/jboss/keycloak/themes/
# COPY ./deployments /opt/jboss/keycloak/standalone/deployments

EXPOSE 8080