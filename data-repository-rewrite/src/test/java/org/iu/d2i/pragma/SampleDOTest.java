
package org.iu.d2i.pragma;
/*
 *
 * Copyright 2015 The Trustees of Indiana University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author kunarath@iu.edu
 */

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.MultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;
import org.apache.log4j.Logger;
import org.iu.d2i.pragma.response.MessageResponse;
import org.junit.Test;
import org.apache.log4j.PropertyConfigurator;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.*;

public class SampleDOTest{

	private final static Logger logger = Logger.getLogger(SampleDOTest.class);
	// Need to change according to your localhost URL
	String repo_uri = "http://localhost:8080/data-repository-rewrite/";

	@Test
	public void DOSampleProcess() throws Exception {

	// Set up basic logging mechanism
		ClassLoader classLoader = getClass().getClassLoader();
		File log4j_file = new File(classLoader.getResource("log4j.properties").getFile());
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream(log4j_file));
		PropertyConfigurator.configure(logProperties);

		logger.info("Run Sample DO upload and register process...");
		logger.info("Step 1 - Upload DO with metadata object to backend repository...");

		// Step 1: Upload DO with metadata object to backend database
		// Construct Metadata
		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode node = nodeFactory.objectNode();

		node.put("OccurrenceSetID", "2578");
		node.put("displayName", "57");
		node.put("lastModified", "2016-01-16T01:52:13");
		node.put("DataType", "20.5000.239/9e873b2a5690da5b0455");
		node.put("DOname", "LM Occurrence Bironella gracilis");
		node.put("downloadingURL", "");

		// Construct data as multipart file
		File file = new File(classLoader.getResource("occur_2578.zip").getFile());

		FormDataMultiPart data = new FormDataMultiPart();
		data.field("file", file.getName());
		FormDataBodyPart file_data = new FormDataBodyPart("data",
				new FileInputStream(file),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		data.bodyPart(file_data);

		logger.info("Upload DO Name:" + "LM Occurrence Bironella gracilis");
		logger.info("Upload DO data type:" + "20.5000.239/9e873b2a5690da5b0455");
		logger.info("Upload DO downloading URL:");
		logger.info("Upload metadata object:" + node.toString());

		String id = DOUpload(node.toString(), file);

		logger.info("Object StagingDB ID is:" + id);

		/*
		 * Step 2 can be ignored for middleware service actor // Step 2: Find DO
		 * in StagingDB and perform add or edit operation logger.info(
		 * "Step 2 - Check uploaded DO and metadata object..."); // Find DO
		 * metadata object MvcResult find_metadata_result =
		 * mockMvc.perform(get("/DO/find/metadata").param("ID", id))
		 * .andExpect(status().isOk()).andExpect(content().contentType(MediaType
		 * .APPLICATION_JSON_UTF8))
		 * .andExpect(jsonPath("$.success").value(true)).andReturn(); String
		 * find_metadata_response =
		 * find_metadata_result.getResponse().getContentAsString();
		 * MessageResponse find_metadata_json = new
		 * ObjectMapper().readValue(find_metadata_response,
		 * MessageResponse.class);
		 * System.out.println(find_metadata_json.getMessage()); logger.info(
		 * "DO and metadata object response:" +
		 * find_metadata_json.getMessage());
		 *
		 * // Find DO data MvcResult find_data_result =
		 * mockMvc.perform(get("/DO/find/data").param("ID",
		 * id)).andExpect(status().isOk()) .andReturn(); byte[] find_data_out =
		 * find_data_result.getResponse().getContentAsByteArray();
		 * FileUtils.writeByteArrayToFile(new File("test.zip"), find_data_out);
		 */

		// Add: add DO to permanent Repo and register with a PID
		// Edit: Further edit the DO with updated information
		// For demo we run ADD operation
		logger.info("Step 3 - Replicate DO to permanent Repository...");
		String repoID = DOAdd(id);

		logger.info("Registered PID Handle record:" + repoID);

		data.bodyPart(file_data).close();
	}

	@Test
	public void AgSampleProcess() throws Exception {
		// Set up basic logging mechanism
		ClassLoader classLoader = getClass().getClassLoader();
		File log4j_file = new File(classLoader.getResource("log4j.properties").getFile());
		Properties logProperties = new Properties();
		logProperties.load(new FileInputStream(log4j_file));
		PropertyConfigurator.configure(logProperties);

		logger.info("Run Sample DO upload and register process...");
		logger.info("Step 1 - Upload DO with metadata object to backend repository...");

		// Step 1: Upload DO with metadata object to backend database
		// Construct Metadata
		String input_path = "simulator.properties.txt";
		String output_path = "ward_10_numSeedHH_100_AgYear_2006_allocation_0__soilType_WI_VRZM080_searchScope_5_sharingPercent_0.1_stats.txt";
		File input_file = new File(classLoader.getResource(input_path).getFile());
		File output_file = new File(classLoader.getResource(output_path).getFile());

		JsonNodeFactory nodeFactory = JsonNodeFactory.instance;
		ObjectNode input_node = nodeFactory.objectNode();
		ObjectNode output_node = nodeFactory.objectNode();

		Map<String, String> input_fields = AgFiles2Map(input_file, "=");
		Map<String, String> output_fields = AgFiles2Map(output_file, ":");

		String input_DOname = input_path.replace(".txt", "");
		String output_DOname = output_path.replace(".txt", "");
		if (output_DOname.contains("_stats")) {
			output_DOname = output_DOname.replace("_stats", "");
		}
		// get input metadata
		for (Map.Entry<String, String> entry : input_fields.entrySet()) {
			if (entry.getKey().startsWith("model.parameter"))
				input_node.put(entry.getKey().replace(".", "_"), entry.getValue());
		}
		input_node.put("DataType", "20.5000.239/cd83686e94b6328b28da");
		input_node.put("DOname", input_DOname);
		input_node.put("downloadingURL", "");

		// get output metadata
		String ward_num = output_path.split("_")[1].trim();
		output_node.put("ward", ward_num);
		for (Map.Entry<String, String> entry : output_fields.entrySet()) {
			output_node.put(entry.getKey(), entry.getValue());
		}

		output_node.put("DataType", "20.5000.239/21059cc2035443c2fec5");
		output_node.put("DOname", output_DOname);
		output_node.put("downloadingURL", "");

		// Construct data as multipart file

		FormDataMultiPart input_data = new FormDataMultiPart();
		input_data.field("file", input_file.getName());
		FormDataBodyPart file_input_data = new FormDataBodyPart("data",
				new FileInputStream(input_file),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		input_data.bodyPart(file_input_data);

		FormDataMultiPart output_data = new FormDataMultiPart();
		input_data.field("file", output_file.getName());
		FormDataBodyPart file_output_data = new FormDataBodyPart("data",
				new FileInputStream(output_file),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		output_data.bodyPart(file_output_data);

		String input_id = DOUpload(input_node.toString(), input_file);
		String output_id = DOUpload(output_node.toString(), output_file);

		logger.info("Input Object StagingDB ID is:" + input_id);
		logger.info("Output Object StagingDB ID is:" + output_id);

		// Add: add DO to permanent Repo and register with a PID
		// Edit: Further edit the DO with updated information
		// For demo we run ADD operation
		logger.info("Step 3 - Register DO with handle record and PID metadata profile...");
		String input_repoID = DOAdd(input_id);
		String output_repoID = DOAdd(output_id);

		logger.info("Registered Input PID Handle record:" + input_repoID);
		logger.info("Registered Output PID Handle record:" + output_repoID);


		input_data.bodyPart(file_input_data).close();
		output_data.bodyPart(file_output_data).close();
	}

	public Map<String, String> AgFiles2Map(File file, String split) throws Exception {
		FileInputStream fis = new FileInputStream(file);

		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		Map<String, String> output = new HashMap<String, String>();

		String line = null;
		while ((line = br.readLine()) != null) {
			if (!line.startsWith("##")) {
				String[] tokens = line.split(split);
				output.put(tokens[0].trim(), tokens[1].trim());
			}
		}

		br.close();

		return output;
	}

	public static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		final StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	public String DOUpload(String metadata, File data) throws Exception {

		WebResource uploadDOResource = Client.create().resource(repo_uri + "DO/upload");
		FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("data", data,
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		fileDataBodyPart.setContentDisposition(
				FormDataContentDisposition.name("data").fileName(data.getName()).build());

		// Setup Multipart Upload Content
		final MultiPart uploadDOMultiPart = new FormDataMultiPart()
				.field("metadata", metadata, MediaType.APPLICATION_JSON_TYPE).bodyPart(fileDataBodyPart);
		uploadDOMultiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);

		// POST request final
		ClientResponse uploadDOResponse = uploadDOResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(ClientResponse.class,
				uploadDOMultiPart);
		String staging_id = SampleDOTest.getStringFromInputStream(uploadDOResponse.getEntityInputStream());
		return staging_id;
	}

	public String DOAdd(String id) throws Exception {

		WebResource addDOResource = Client.create().resource(repo_uri + "DO/add");

		final MultiPart addMultiPart = new FormDataMultiPart().field("ID", id);
		addMultiPart.setMediaType(MediaType.APPLICATION_JSON_TYPE);
		MessageResponse addDOResponse = addDOResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).post(MessageResponse.class,
				addMultiPart);
		return addDOResponse.getMessage();
	}
}