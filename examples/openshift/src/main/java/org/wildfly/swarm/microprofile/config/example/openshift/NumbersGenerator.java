package org.wildfly.swarm.microprofile.config.example.openshift;

import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/")
public class NumbersGenerator {

    @Inject
    @ConfigProperty(name = "num.size", defaultValue = "3")
    int numSize;

    @Inject
    @ConfigProperty(name = "num.max", defaultValue = "" + Integer.MAX_VALUE)
    int numMax;

    private final Random random = new Random();

    @GET
    @Produces("text/plain")
    public Response doGet() {
        String result = random.ints(numSize, 0, numMax)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining("\n"));
        return Response.ok(result).build();
    }
}