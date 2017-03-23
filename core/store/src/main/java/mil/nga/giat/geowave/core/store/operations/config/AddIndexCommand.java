package mil.nga.giat.geowave.core.store.operations.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import mil.nga.giat.geowave.core.cli.api.Operation;
import mil.nga.giat.geowave.core.cli.parser.ManualOperationParams;
import org.shaded.restlet.resource.Get;
import org.shaded.restlet.data.Status;
import org.shaded.restlet.resource.Post;
import org.shaded.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import mil.nga.giat.geowave.core.cli.api.DefaultOperation;

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

@GeowaveOperation(name = "addindex", parentOperation = ConfigSection.class)
@Parameters(commandDescription = "Configure an index for usage in GeoWave")
public class AddIndexCommand extends
		ServerResource implements
		Command
{
	private final static Logger LOGGER = LoggerFactory.getLogger(AddIndexCommand.class);

	@Parameter(description = "<name>", required = true)
	private List<String> parameters = new ArrayList<>();

	@Parameter(names = {
		"-d",
		"--default"
	}, description = "Make this the default index creating stores")
	private Boolean makeDefault;

	@Parameter(names = {
		"-t",
		"--type"
	}, required = true, description = "The type of index, such as spatial, or spatial_temporal")
	private String type;

	@ParametersDelegate
	private IndexPluginOptions pluginOptions = new IndexPluginOptions();

	@Override
	public boolean prepare(
			OperationParams params ) {

		// Load SPI options for the given type into pluginOptions.
		if (type != null) {
			pluginOptions.selectPlugin(type);
		}
		else {
			// Try to load the 'default' options.

			File configFile = (File) params.getContext().get(
					ConfigOptions.PROPERTIES_FILE_CONTEXT);
			Properties existingProps = ConfigOptions.loadProperties(
					configFile,
					null);

			String defaultIndex = existingProps.getProperty(IndexPluginOptions.DEFAULT_PROPERTY_NAMESPACE);

			// Load the default index.
			if (defaultIndex != null) {
				try {
					if (pluginOptions.load(
							existingProps,
							IndexPluginOptions.getIndexNamespace(defaultIndex))) {
						// Set the required type option.
						this.type = pluginOptions.getType();
					}
				}
				catch (ParameterException pe) {
					LOGGER.warn(
							"Couldn't load default index: " + defaultIndex,
							pe);
				}
			}
		}

		// Successfully prepared.
		return true;
	}

	@Override
	public void execute(
			OperationParams params ) {
		computeResults(params);

	}

	@Get
	public void restGet() {
		this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
		return;
	}

	@Post("json")
	public void restPost() {

		// from this class
		makeDefault = getQueryValue("default") == "true";
		type = getQueryValue("type");
		String name = getQueryValue("name");
		if (name == null) {
			this.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
			return;
		}
		parameters.add(name);

		// from IndexPluginOptions
		String numPartitions = getQueryValue("numPartitions");
		String partitionStrategy = getQueryValue("partitionStrategy");

		OperationParams params = new ManualOperationParams();

		params.getContext().put(
				"name",
				name);
		params.getContext().put(
				"numPartitions",
				numPartitions);
		params.getContext().put(
				"partitionStrategy",
				partitionStrategy);
		params.getContext().put(
				ConfigOptions.PROPERTIES_FILE_CONTEXT,
				ConfigOptions.getDefaultPropertyFile());

		prepare(params);
		computeResults(params);
	}

	private void computeResults(
			OperationParams params ) {

		// Ensure that a name is chosen.
		if (parameters.size() != 1) {
			throw new ParameterException(
					"Must specify index name");
		}

		File propFile = (File) params.getContext().get(
				ConfigOptions.PROPERTIES_FILE_CONTEXT);
		Properties existingProps = ConfigOptions.loadProperties(
				propFile,
				null);

		// Make sure we're not already in the index.
		IndexPluginOptions existPlugin = new IndexPluginOptions();
		if (existPlugin.load(
				existingProps,
				getNamespace())) {
			throw new ParameterException(
					"That index already exists: " + getPluginName());
		}

		// Save the options.
		pluginOptions.save(
				existingProps,
				getNamespace());

		// Make default?
		if (Boolean.TRUE.equals(makeDefault)) {
			existingProps.setProperty(
					IndexPluginOptions.DEFAULT_PROPERTY_NAMESPACE,
					getPluginName());
		}

		// Write properties file
		ConfigOptions.writeProperties(
				propFile,
				existingProps);
	}

	public IndexPluginOptions getPluginOptions() {
		return pluginOptions;
	}

	public String getPluginName() {
		return parameters.get(0);
	}

	public String getNamespace() {
		return IndexPluginOptions.getIndexNamespace(getPluginName());
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(
			String indexName ) {
		this.parameters = new ArrayList<String>();
		this.parameters.add(indexName);
	}

	public Boolean getMakeDefault() {
		return makeDefault;
	}

	public void setMakeDefault(
			Boolean makeDefault ) {
		this.makeDefault = makeDefault;
	}

	public String getType() {
		return type;
	}

	public void setType(
			String type ) {
		this.type = type;
	}

	public void setPluginOptions(
			IndexPluginOptions pluginOptions ) {
		this.pluginOptions = pluginOptions;
	}
}
