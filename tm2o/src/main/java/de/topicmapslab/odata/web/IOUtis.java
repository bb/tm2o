package de.topicmapslab.odata.web;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import de.topicmapslab.odata.TopicMapODataProducerFactory;
import de.topicmapslab.odata.config.EContentProviderConfiguration;
import de.topicmapslab.odata.content.IOdataContentProvider;
import de.topicmapslab.odata.content.memory.MemoryOdataContentProvider;
import de.topicmapslab.odata.content.remote.RemoteOdataContentProvider;

public class IOUtis {

	/**
	 * Utility method to parse multi-part content of the POST request
	 * 
	 * @param request
	 *            the request
	 * @return the configuration extracted from POST request
	 */
	public static ODataConfiguration post(HttpServletRequest request) {
		try {
			/*
			 * Create a factory for disk-based file items
			 */
			DiskFileItemFactory factory = new DiskFileItemFactory();
			/*
			 * create variables to fill
			 */
			boolean flatAssociation = false;
			boolean local = false;
			String namespace = null;
			String address = null;
			String apiKey = null;
			File f = null;
			/*
			 * Create a new file upload handler
			 */
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<?> items = upload.parseRequest(request);
			Iterator<?> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				String name = item.getFieldName();
				String value = item.getString();
				/*
				 * is normal form field
				 */
				if (item.isFormField()) {
					if ("flat".equalsIgnoreCase(name)) {
						flatAssociation = true;
					} else if ("locPro".equalsIgnoreCase(name)) {
						local = true;
					} else if ("address".equalsIgnoreCase(name)) {
						address = value;
					} else if ("namespace".equalsIgnoreCase(name)) {
						namespace = value;
					} else if ("api-key".equalsIgnoreCase(name)) {
						apiKey = value;
					}
				}
				/*
				 * is file upload
				 */
				else if (name.equalsIgnoreCase("file") && !item.getName().isEmpty()) {
					InputStream uploadedStream = item.getInputStream();
					try {
						File path = new File(TopicMapODataProducerFactory.PATH);
						if (!path.exists()) {
							path.mkdirs();
						}
						f = new File(TopicMapODataProducerFactory.PATH + "/" + item.getName());

						System.out.println(f.getAbsolutePath());
						if (!f.exists()) {
							f.createNewFile();
						}
					} catch (Exception e) {
						e.printStackTrace(System.out);
						System.err.println("Cannot copy file, use tempory file");
						f = File.createTempFile("tm2o", "tm");
					}
					FileWriter writer = new FileWriter(f);
					int i = uploadedStream.read();
					while (i != -1) {
						writer.write(i);
						i = uploadedStream.read();
					}
					writer.flush();
					writer.close();
					uploadedStream.close();
				}
			}
			/*
			 * create configuration
			 */
			Properties properties = new Properties();
			properties.put("association-mode", flatAssociation ? EContentProviderConfiguration.FLAT_ASSOCIATION.name()
					: EContentProviderConfiguration.STRONG_ASSOCIATION.name());
			if (address != null) {
				properties.put("server", address);
			}
			if (apiKey != null) {
				properties.put("api-key", apiKey);
			}
			if (f != null) {
				properties.put("file", f);
				properties.put("path", f.getAbsolutePath());
			}
			Class<? extends IOdataContentProvider> clazz = local ? MemoryOdataContentProvider.class : RemoteOdataContentProvider.class;
			return new ODataConfiguration(clazz, properties, namespace);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Utility method to fetch all topic map IDs from server
	 * 
	 * @param serverAddress
	 *            the server address
	 * 
	 * @param apiKey
	 *            the API key
	 * @return a possible empty map of locator and ID
	 */
	public static Map<String, String> getTopicMapIds(final String serverAddress, final String apiKey) {
		Map<String, String> topicMaps = new HashMap<String, String>();

		try {
			/*
			 * open streams
			 */
			URL url = new URL(serverAddress + "/tm/topicmaps?apikey=" + apiKey);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			/*
			 * proceed response as extended JTMQR
			 */
			InputStream is = connection.getInputStream();
			ObjectMapper m = new ObjectMapper();
			JsonNode rootNode = m.readValue(is, JsonNode.class);
			JsonNode data = rootNode.path("data");
			if (!data.isMissingNode()) {
				JsonNode tm = data.path("topicmaps");
				if (!tm.isMissingNode()) {
					Iterator<JsonNode> tms = tm.getElements();
					while (tms.hasNext()) {
						JsonNode n = tms.next();
						topicMaps.put(n.get("locator").getTextValue(), n.get("id").getTextValue());
					}
				}
			}
		} catch (Exception e) {
			return null;
		}

		return topicMaps;
	}

}
