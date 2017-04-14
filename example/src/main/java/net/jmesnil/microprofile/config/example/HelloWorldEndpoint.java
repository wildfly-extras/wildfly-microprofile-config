package net.jmesnil.microprofile.config.example;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;


@Path("/hello")
public class HelloWorldEndpoint {

	@Inject
	Config config;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		String text = "FOO_BAR property is " + config.getOptionalValue("FOO_BAR", String.class);
		return Response.ok(text).build();
	}
}