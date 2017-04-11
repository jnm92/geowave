package mil.nga.giat.geowave.service.rest;

import java.util.ArrayList;

import org.reflections.Reflections;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;

import org.shaded.restlet.Application;
import org.shaded.restlet.Component;
import org.shaded.restlet.Server;
import org.shaded.restlet.data.MediaType;
import org.shaded.restlet.data.Protocol;
import org.shaded.restlet.representation.Representation;
import org.shaded.restlet.resource.Get;
import org.shaded.restlet.resource.ResourceException;
import org.shaded.restlet.resource.ServerResource;
import org.shaded.restlet.resource.Status;
import org.shaded.restlet.routing.Router;
import org.shaded.restlet.Restlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.shaded.restlet.ext.fileupload.RestletFileUpload;
import org.shaded.restlet.representation.StringRepresentation;
import org.shaded.restlet.resource.Post;

public class RestServer extends
		ServerResource
{
	private ArrayList<Route> availableRoutes;

	/**
	 * Run the Restlet server (localhost:5152)
	 */
	public static void main(
			String[] args ) {
		RestServer server = new RestServer();
		server.run(5152);
	}

	public RestServer() {
		this.availableRoutes = new ArrayList<Route>();

		for (Class<?> operation : new Reflections(
				"mil.nga.giat.geowave").getTypesAnnotatedWith(GeowaveOperation.class)) {
			availableRoutes.add(new Route(
					operation));
		}
	}

	// Show a simple 404 if the route is unknown to the server
	@Get("html")
	public String toString() {
		StringBuilder routeStringBuilder = new StringBuilder(
				"Available Routes: (geowave/help is only that currently extends ServerResource)<br>");
		for (Route route : availableRoutes) {
			routeStringBuilder.append(route.getPath() + " -> " + route.getOperationAsGeneric() + "<br>");
		}
		return "<b>404</b>: Route not found<br><br>" + routeStringBuilder.toString();
	}

	public void run(
			int port ) {

		// Add paths for each command
		final Router router = new Router();
		for (Route route : availableRoutes) {
			if (route.isServerResource()) {
				router.attach(
						route.getPath(),
						route.getOperationAsResource());
			}
			else {
				router.attach(
						route.getPath(),
						NonResourceCommand.class);
			}
		}
		// Provide basic 404 error page for unknown route
		router.attachDefault(RestServer.class);

		// Setup router
		Application myApp = new Application() {
			@Override
			public Restlet createInboundRoot() {
				router.setContext(getContext());
				return router;
			};
		};
		Component component = new Component();
		component.getDefaultHost().attach(
				"/",
				myApp);

		// Start server
		try {
			new Server(
					Protocol.HTTP,
					port,
					component).start();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not create Restlet server - is the port already bound?");
		}
	}

	/**
	 * Holds necessary information to create a Restlet route
	 */
	private static class Route
	{
		private String path;
		private Class<?> operation;
		private boolean serverResource;

		/**
		 * Create a new route given an operation
		 * 
		 * @param operation
		 */
		public Route(
				Class<?> operation ) {
			this.path = pathFor(
					operation).substring(
					1);
			this.operation = operation;

			// check if operation extends ServerResource, which is required for
			// Restlet to
			// generate a route from the class
			try {
				operation.asSubclass(ServerResource.class);
				serverResource = true;
			}
			catch (ClassCastException e) {
				serverResource = false;
			}
		}

		/**
		 * @return true if the route represents an operation which extends
		 *         ServerResource
		 */
		public boolean isServerResource() {
			return serverResource;
		}

		/**
		 * Return the operation as its ServerResource subclass if operation
		 * extends ServerResource, otherwise return null.
		 * 
		 * @return the operation as its ServerResource subclass, or null if
		 *         operation does not extend ServerResource
		 */
		public Class<? extends ServerResource> getOperationAsResource() {
			if (serverResource) {
				return operation.asSubclass(ServerResource.class);
			}
			else {
				return null;
			}
		}

		/**
		 * Return the operation as it was originally passed
		 * 
		 * @return
		 */
		public Class<?> getOperationAsGeneric() {
			return operation;
		}

		/**
		 * Get the path that represents the route
		 * 
		 * @return a string representing the path, specified by pathFor
		 */
		public String getPath() {
			return path;
		}

		/**
		 * Get the path for a command based on the operation hierarchy Return
		 * the path as a string in the format "/first/next/next"
		 * 
		 * @param operation
		 *            - the operation to find the path for
		 * @return the formatted path as a string
		 */
		public static String pathFor(
				Class<?> operation ) {
			// Top level of heirarchy
			if (operation == Object.class) {
				return "";
			}
			GeowaveOperation operationInfo = operation.getAnnotation(GeowaveOperation.class);
			return pathFor(operationInfo.parentOperation()) + "/" + operationInfo.name();
		}
	}

	/**
	 * A simple ServerResource to show if the route's operation does not extend
	 * ServerResource
	 */
	public static class NonResourceCommand extends
			ServerResource
	{
		@Get("html")
		public String toString() {
			return "The route exists, but the command does not extend ServerResource";
		}
	}

	/**
	 * ServerResource to handle uploading files. Uses restlet fileupload.
	 */
	@GeowaveOperation(name = "fileuploadserverresource")
	public class FileUploadServerResource extends
			ServerResource
	{

		@Post
		/**
		 * processes uploaded file, storing in memory if small enough, if not, on disk
		 * @param entity
		 * @return text of uploaded file
		 * @throws Exception
		 */
		public Representation accept(
				Representation entity )
				throws Exception {
			Representation result = null; // TODO return JSON
			if (entity != null && MediaType.MULTIPART_FORM_DATA.equals(
					entity.getMediaType(),
					true)) {
				// 1/ Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(1000240);

				// 2/ Create a new file upload handler based on the Restlet
				// FileUpload extension that will parse Restlet requests and
				// generates FileItems.
				RestletFileUpload upload = new RestletFileUpload(
						factory);

				// 3/ Request is parsed by the handler which generates a
				// list of FileItems
				FileItemIterator fileIterator = upload.getItemIterator(entity);

				// Process only the uploaded item called "fileToUpload"
				// and return back
				boolean found = false;
				while (fileIterator.hasNext() && !found) {
					FileItemStream fi = fileIterator.next();

					if (fi.getFieldName().equals(
							"fileToUpload")) {
						// For the matter of sample code, it filters the multi-
						// part form according to the field name.
						found = true;
						// consume the stream immediately, otherwise the stream
						// will be closed.
						StringBuilder sb = new StringBuilder(
								"{\"media type\": \"");
						sb.append(
								fi.getContentType()).append(
								"\",");
						sb.append("\"file name\" : \"");
						sb.append(
								fi.getName()).append(
								"\",");
						sb.append("\"content\": \"");
						BufferedReader br = new BufferedReader(
								new InputStreamReader(
										fi.openStream()));
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						sb.append("\"");
						result = new StringRepresentation(
								sb.toString(),
								MediaType.TEXT_PLAIN);
					}
				}
			}
			else {
				// POST request with no entity.
				// TODO print some kind of error message?
			}

			return result;
		}

	}
}
