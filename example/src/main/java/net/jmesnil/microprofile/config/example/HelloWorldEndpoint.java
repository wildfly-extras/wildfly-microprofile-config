package net.jmesnil.microprofile.config.example;

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
	Config config;

	@Inject
	@ConfigProperty(name = "BAR", defaultValue = "my BAR property comes from the code")
	String bar;

	@GET
	@Produces("text/plain")
	public Response doGet() {
		Optional<String> foo = config.getOptionalValue("FOO", String.class);

		Optional<Boolean> boolProp = config.getOptionalValue("BOOL_PROP", Boolean.class);

		StringBuilder text = new StringBuilder();
		text.append("FOO property = " + foo + "\n");
		text.append("BAR property = " + bar + "\n");
		text.append("BOOL_PROP property = " + boolProp + "\n");
		return Response.ok(text).build();
	}
}