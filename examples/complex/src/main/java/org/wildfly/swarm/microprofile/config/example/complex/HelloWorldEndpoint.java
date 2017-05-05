package org.wildfly.swarm.microprofile.config.example.complex;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/hello")
public class HelloWorldEndpoint {

    @Inject
    @ConfigProperty(name = "my.prop")
    String prop;

    @GET
    @Produces("text/plain")
    public Response doGet() {
        StringBuilder text = new StringBuilder();

        text.append("property my.prop = " + prop + "\n");
        return Response.ok(text).build();
    }
}