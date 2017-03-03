package mil.nga.giat.geowave.core.store.operations.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

// new code
import org.apache.commons.lang3.StringUtils;
import org.restlet.resource.Get;
// end new code

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;
import mil.nga.giat.geowave.core.cli.api.Command;
import mil.nga.giat.geowave.core.cli.api.OperationParams;
import mil.nga.giat.geowave.core.cli.operations.config.ConfigSection;
import mil.nga.giat.geowave.core.cli.operations.config.options.ConfigOptions;
import mil.nga.giat.geowave.core.store.operations.remote.options.IndexPluginOptions;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;

@GeowaveOperation(name = "cpindex", parentOperation = ConfigSection.class)
@Parameters(commandDescription = "Copy and modify existing index configuration")
public class CopyIndexCommand implements
		Command
{
	// new code
	private static int SUCCESS = 0;
	private static int USAGE_ERROR = -1;
	private static int INDEX_EXISTS = -2;
	// end new code

	@Parameter(description = "<name> <new name>")
	private List<String> parameters = new ArrayList<String>();

	@Parameter(names = {
		"-d",
		"--default"
	}, description = "Make this the default index creating stores")
	private Boolean makeDefault;

	@ParametersDelegate
	private IndexPluginOptions newPluginOptions = new IndexPluginOptions();

	private File configFile;
	private Properties existingProps;

	@Override
	public boolean prepare(
			OperationParams params ) {

		configFile = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);
		existingProps = ConfigOptions.loadProperties(
				configFile,
				null);

		// Load the old index, so that we can override the values
		String oldIndex = null;
		if (parameters.size() >= 1) {
			oldIndex = parameters.get(0);
			if (!newPluginOptions.load(
					existingProps,
					IndexPluginOptions.getIndexNamespace(oldIndex))) {
				throw new ParameterException(
						"Could not find index: " + oldIndex);
			}
		}

		// Successfully prepared.
		return true;
	}

	@Override
	public void execute(
			OperationParams params ) {

		String result = computeResults(params);

	}

	@Get("json")
	public String computeResults(
			OperationParams params ) {
		// TODO
		String key = getQueryValue("key");
		String value = getQueryValue("value");

		if ((key == null || key.equals("")) || value == null) {
			return "{ \"result\":" + USAGE_ERROR
					+ ", \"message\":\"Must specify existing index and new index (comma separated)\",\"prev\":\"\"}";
		}

		setParameters(
				key,
				value);
		// OperationParams params = new ManualOperationParams();
		// TODO just adding this file information causes the config file to
		// be stored as 'unknownversion-config.properties' which probably
		// should change..
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				ConfigOptions.getDefaultPropertyFile());

		Result result = copyIndex(params);

		if (result.result == USAGE_ERROR) {
			return "{ \"result\":" + USAGE_ERROR + ", \"message\":\"usage error\":\"" + "\"}";
		}
		else if (result.result == INDEX_EXISTS) {
			return "{ \"result\":" + INDEX_EXISTS + ", \"message\":\"index exists error\",\"indexname\":\""
					+ result.newIndex + "\"}";
		}
		else {
			return "{ \"result\":" + SUCCESS + ", \"message\":\"\",\"indexname\":\"" + result.newIndex + "\"}";
		}
	}

	/* TODO */
	private Result copyIndex(
			OperationParams params ) {
		Result result = new Result();

		if (parameters.size() < 2) {
			// throw new ParameterException(
			// "Must specify <existing index> <new index> names");
			result.result = USAGE_ERROR;
			return result;
		}

		// This is the new index name.
		String newIndex = parameters.get(1);
		String newIndexNamespace = IndexPluginOptions.getIndexNamespace(newIndex);
		result.newIndex = newIndex;

		// Make sure we're not already in the index.
		IndexPluginOptions existPlugin = new IndexPluginOptions();
		if (existPlugin.load(
				existingProps,
				newIndexNamespace)) {
			// throw new ParameterException(
			// "That index already exists: " + newIndex);
			result.result = INDEX_EXISTS;
			return result;
		}

		// Save the options.
		newPluginOptions.save(
				existingProps,
				newIndexNamespace);

		// Make default?
		if (Boolean.TRUE.equals(makeDefault)) {
			existingProps.setProperty(
					IndexPluginOptions.DEFAULT_PROPERTY_NAMESPACE,
					newIndex);
		}

		// Write properties file
		ConfigOptions.writeProperties(
				configFile,
				existingProps);

		result.result = SUCCESS;
		return result;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(
			String existingIndex,
			String newIndex ) {
		this.parameters = new ArrayList<String>();
		this.parameters.add(existingIndex);
		this.parameters.add(newIndex);
	}

	// new code
	private static class Result
	{
		int result;
		String newIndex; // TODO update for this class
	}
	// end new code

}
