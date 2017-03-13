package mil.nga.giat.geowave.core.store.operations.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.restlet.resource.Get;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;
import mil.nga.giat.geowave.core.cli.api.Command;
import mil.nga.giat.geowave.core.cli.api.DefaultOperation;
import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.ConfigSection;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexGroupPluginOptions;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexPluginOptions;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;

@GeowaveOperation(name = "addindexgrp", parentOperation = ConfigSection.class)
@Parameters(commandDescription = "Create an index group for usage in GeoWave")
public class AddIndexGroupCommand extends
		DefaultOperation implements
		Command
{
	private static int SUCCESS = 0;
	private static int USAGE_ERROR = -1;
	private static int INDEXING_ERROR = -2;
	private static int GROUP_EXISTS = -3;

	@Parameter(description = "<name> <comma separated list of indexes>")
	private List<String> parameters = new ArrayList<String>();

	@Override
	public void execute(
			OperationParams params ) {
		// Result result = AddIndexGroup(params);
		Result resultMessage = addIndexGroup(params);
		if (resultMessage.result == USAGE_ERROR) {
			throw new ParameterException(
					"Must specify index group name and index names (comma separated)");
		}
	}

	// TODO add post functionality?

	/**
	 * Add rest endpoint for the addIndexGroup command. Looks for GET params
	 * with keys 'key' and 'value' to set.
	 * 
	 * @return string containing json with details of success or failure of the
	 *         index group addition
	 */
	@Get("json")
	public String computeResults() {
		String key = getQueryValue("key");
		String value = getQueryValue("value");
		if ((key == null || key.equals("")) || value == null) {
			return "{ \"result\":"
					+ USAGE_ERROR
					+ ", \"message\":\"Must specify index group name and index names (comma separated)\",\"prev\":\"\"}";
		}

		setParameters(
				key,
				value);
		OperationParams params = new ManualOperationParams();
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				ConfigOptions.getDefaultPropertyFile());

		Result result = addIndexGroup(params);

		if (result.result == INDEXING_ERROR) {
			return "{ \"result\":" + INDEXING_ERROR + ", \"message\":\"indexing error\",\"groupname\":\""
					+ result.groupName + "\"}";
		}
		else if (result.result == GROUP_EXISTS) {
			return "{ \"result\":" + GROUP_EXISTS + ", \"message\":\"group exists error\",\"groupname\":\""
					+ result.groupName + "\"}";
		}
		else {
			return "{ \"result\":" + SUCCESS + ", \"message\":\"\",\"groupname\":\"" + result.groupName + "\"}";
		}

	}

	/**
	 * Adds index group
	 * 
	 * @return result of operation (type of failure or success)
	 */
	private Result addIndexGroup(
			OperationParams params ) {
		Result result = new Result();
		File propFile = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);
		Properties existingProps = ConfigOptions.loadProperties(
				propFile,
				null);

		if (parameters.size() < 2) {
			// throw new ParameterException(
			// "Must specify index group name and index names (comma separated)");
			result.result = USAGE_ERROR;
			return result;
		}

		// New index group name
		String newGroupName = parameters.get(0);
		result.groupName = newGroupName;
		String[] indexes = parameters.get(
				1).split(
				",");

		// Make sure the existing group doesn't exist.
		IndexGroupPluginOptions groupOptions = new IndexGroupPluginOptions();
		if (groupOptions.load(
				existingProps,
				getNamespace())) {
			// throw new ParameterException(
			// "That index group already exists: " + newGroupName);
			result.result = GROUP_EXISTS;
			return result;
		}

		// Make sure all the indexes exist, and add them to the group options.
		for (int i = 0; i < indexes.length; i++) {
			indexes[i] = indexes[i].trim();
			IndexPluginOptions options = new IndexPluginOptions();
			if (!options.load(
					existingProps,
					IndexPluginOptions.getIndexNamespace(indexes[i]))) {
				// throw new ParameterException(
				// "That index does not exist: " + indexes[i]);
				result.result = INDEXING_ERROR;
				return result;
			}
			groupOptions.getDimensionalityPlugins().put(
					indexes[i],
					options);
		}

		// Save the group
		groupOptions.save(
				existingProps,
				getNamespace());

		// Write to disk.
		ConfigOptions.writeProperties(
				propFile,
				existingProps);
		result.result = SUCCESS;
		return result;
	}

	public String getPluginName() {
		return parameters.get(0);
	}

	public String getNamespace() {
		return IndexGroupPluginOptions.getIndexGroupNamespace(getPluginName());
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(
			String name,
			String commaSeparatedIndexes ) {
		this.parameters = new ArrayList<String>();
		this.parameters.add(name);
		this.parameters.add(commaSeparatedIndexes);
	}

	private static class Result
	{
		int result;
		String groupName;
	}

}
