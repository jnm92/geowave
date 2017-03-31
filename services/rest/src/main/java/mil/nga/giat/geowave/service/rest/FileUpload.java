package mil.nga.giat.geowave.service.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import mil.nga.giat.geowave.core.cli.annotations.GeowaveOperation;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
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

/**
 * ServerResource to handle uploading files. Uses restlet fileupload.
 */
@GeowaveOperation(name = "fileupload")
public class FileUpload extends
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
