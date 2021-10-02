package com.github.hlucasfranca.keycloak.providers;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
public interface UserApiService {

    @GET
    @Path("/{username}")
    User getUserDetails(@PathParam("username") String username);

    @POST
    @Path("/{username}/verify-password")
    VerifyPasswordResponse verifyUserPassword(@PathParam("username") String username, String password);
}
