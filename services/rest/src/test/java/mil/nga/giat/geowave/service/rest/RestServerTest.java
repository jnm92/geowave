package mil.nga.giat.geowave.service.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.shaded.restlet.Response;
import org.shaded.restlet.Client;
import org.shaded.restlet.Request;
import org.shaded.restlet.data.MediaType;
import org.shaded.restlet.data.Method;
import org.shaded.restlet.data.Protocol;
import org.shaded.restlet.data.Status;
import org.shaded.restlet.representation.Representation;
import org.shaded.restlet.resource.ClientResource;
import org.shaded.restlet.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class RestServerTest
{
	@BeforeClass
	public static void runServer() {
		RestServer.main(new String[] {});
	}

	public Response request(
			String path,
			Method method) {

		Client client = new Client(
				Protocol.HTTP);
		Request request = new Request(
				method,
				"http://localhost:5152/" + path);

		return client.handle(request);
	}

	// Tests geowave/remote/clear
	@Test
	public void geowave_remote_clear() {
		// Response = request(
		// "geowave/remote/clear",
		// Method.GET);
	}

	// Tests geowave/config/rmindexgrp
	@Test
	public void geowave_config_rmindexgrp() {
		Response = request(
		"geowave/config/rmindexgrp",
		Method.POST);
	}

	// Tests geowave/remote/rmadapter
	@Test
	public void geowave_remote_rmadapter() {
		// Response = request(
		// "geowave/remote/rmadapter",
		// Method.GET);
	}

	// Tests geowave/config/addindexgrp
	@Test
	public void geowave_config_addindexgrp() {
		// Response = request(
		// "geowave/config/addindexgrp",
		// Method.GET);
	}

	// Tests geowave/explain
	@Test
	public void geowave_explain() {
		// Response = request(
		// "geowave/explain",
		// Method.GET);
	}

	// Tests geowave/config/addindex
	@Test
	public void geowave_config_addindex() {
		// Response = request(
		// "geowave/config/addindex",
		// Method.GET);
	}

	// Tests geowave/remote/listindex
	@Test
	public void geowave_remote_listindex() {
		// Response = request(
		// "geowave/remote/listindex",
		// Method.GET);
	}

	// Tests geowave/config/list
	@Test
	public void geowave_config_list()
			throws IOException,
			ParseException {

		OperationParams params = new ManualOperationParams();
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				ConfigOptions.getDefaultPropertyFile());
		File f = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);
		Properties p = ConfigOptions.loadProperties(
				f,
				null);

		String key = "name";
		String value = "value";

		assert p != null;

		p.setProperty(
				key,
				value);
		ConfigOptions.writeProperties(
				f,
				p);

		Response response = request(
				"geowave/config/list"
				Method.GET);

		assertTrue(
				"Status is 200",
				response.getStatus().getCode() == 200);

		assertTrue(
				"Has a body",
				response.isEntityAvailable());

		assertTrue(
				"Body is JSON",
				response.getEntity().getMediaType().equals(
						MediaType.APPLICATION_JSON));

		String text = response.getEntity().getText();

		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(text);

		assertTrue(
				"JSON can be parsed",
				obj != null);

		String name = (String) obj.get("name");

		assertTrue(
				"List contains 'name'",
				name != null);
		assertTrue(
				"'name' is 'value'",
				name.equals("value"));

	}

	// Tests geowave/config/cpindex
	@Test
	public void geowave_config_cpindex() {
		// Response = request(
		// "geowave/config/cpindex",
		// Method.GET);
	}

	// Tests geowave/remote/rmstat
	@Test
	public void geowave_remote_rmstat() {
		// Response = request(
		// "geowave/remote/rmstat",
		// Method.GET);
	}

	// Tests geowave/remote/liststats
	@Test
	public void geowave_remote_liststats() {
		// Response = request(
		// "geowave/remote/liststats",
		// Method.GET);
	}

	// Tests geowave/config/rmstore
	@Test
	public void geowave_config_rmstore() {
		// Response = request(
		// "geowave/config/rmstore",
		// Method.GET);
	}

	// Tests geowave/remote/listadapter
	@Test
	public void geowave_remote_listadapter() {
		// Response = request(
		// "geowave/remote/listadapter",
		// Method.GET);
	}

	// Tests geowave
	@Test
	public void geowave() {
		// Response = request(
		// "geowave",
		// Method.GET);
	}

	// Tests geowave/remote/recalcstats
	@Test
	public void geowave_remote_recalcstats() {
		// Response = request(
		// "geowave/remote/recalcstats",
		// Method.GET);
	}

	// Tests geowave/config
	@Test
	public void geowave_config() {
		// Response = request(
		// "geowave/config",
		// Method.GET);
	}

	// Tests geowave/config/addstore
	@Test
	public void geowave_config_addstore() {
		 Response = request(
		 Method.GET,
		 "geowave/config/addstore");
	}

	// Tests geowave/config/cpstore
	@Test
	public void geowave_config_cpstore() {
		// Response = request(
		// "geowave/config/cpstore",
		// Method.GET);
	}

	// Tests geowave/remote
	@Test
	public void geowave_remote() {
		// Response = request(
		// "geowave/remote",
		// Method.GET);
	}

	// Tests geowave/config/set
	@Test
	public void geowave_config_set() {
		// Response = request(
		// "geowave/config/set",
		// Method.GET);
	}

	// Tests geowave/help
	@Test
	public void geowave_help() {
		// Response = request(
		// "geowave/help",
		// Method.GET);
	}

	// Tests geowave/remote/rmindex
	@Test
	public void geowave_remote_rmindex() {
		// Response = request(
		// "geowave/remote/rmindex",
		// Method.GET);
	}

	// Tests geowave/remote/calcstat
	@Test
	public void geowave_remote_calcstat() {
		// Response = request(
		// "geowave/remote/calcstat",
		// Method.GET);
	}

	// Tests geowave/config/rmindex
	@Test
	public void geowave_config_rmindex() {
	}
}
