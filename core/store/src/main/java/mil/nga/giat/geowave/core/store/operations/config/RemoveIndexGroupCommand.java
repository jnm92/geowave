package mil.nga.giat.geowave.core.store.operations.config;

import org.shaded.restlet.representation.Representation;

import java.io.File;

import org.shaded.restlet.data.Form;
import org.shaded.restlet.data.Status;
import org.shaded.restlet.resource.Post;

import com.beust.jcommander.Parameters;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;
import mil.nga.giat.geowave.core.cli.api.Command;
import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.ConfigSection;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexGroupPluginOptions;

@GeowaveOperation(name = "rmindexgrp", parentOperation = ConfigSection.class)
@Parameters(commandDescription = "Remove index group from Geowave configuration")
public class RemoveIndexGroupCommand extends
		AbstractRemoveCommand implements
		Command
{

	public void computeResults(
			OperationParams params ) {

		// Search for properties relevant to the given name
		String pattern = IndexGroupPluginOptions.getIndexGroupNamespace(getEntryName());
		super.computeResults(
				params,
				pattern);

	}

	@Override
	public void execute(
			OperationParams params ) {
		computeResults(params);
	}

	@Post("form:json")
	public void restPost(
			Representation entity ) {
		Form form = new Form(
				entity);
		String name = form.getFirstValue("name");
		// String name = getQueryValue("name");

		String configFileParameter = form.getFirstValue("config_file");
		File configFile = (configFileParameter != null) ? new File(
				configFileParameter) : ConfigOptions.getDefaultPropertyFile();

		if (name == null) {
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		setEntryName(name);
		OperationParams params = new ManualOperationParams();
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				configFile);
		computeResults(params);
	}
}
