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

package org.iu.d2i.pragma;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;
import com.sun.jersey.core.header.FormDataContentDisposition;
import org.iu.d2i.pragma.mongo.PermanentRepository;
import org.iu.d2i.pragma.mongo.StagingDBRepository;
import org.iu.d2i.pragma.response.MessageListResponse;
import org.iu.d2i.pragma.response.MessageResponse;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@Path("/DO")
public class DataObjectController {

    private StagingDBRepository staging_repository = new StagingDBRepository();

    private PermanentRepository permanent_repository = new PermanentRepository();

	@POST
	@Path("/upload")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOupload(String metadata,
									FormDataContentDisposition file,
									InputStream inputStream
									) {
		if (metadata == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("metadata parameter is mandatory")
							.build()
			);
		}
		try {
			// Create metadata DBObject from input
			DBObject metadataObject = (DBObject) JSON.parse(metadata);

			// Ingest multipart file into inputstream;
			String file_name = file.getFileName();
			String content_type = file.getType();
			// Connect to MongoDB and use GridFS to store metadata and data
			// Return created DO internal id in stagingDB
			String id = staging_repository.addDO(inputStream, file_name, content_type, metadataObject);
			MessageResponse response = new MessageResponse(true, id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@GET
	@Path("/find/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOfindMedata(@QueryParam("ID") String ID) {
		// Connect to MongoDB and return DO metadata as response
		// return
		if (ID == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			GridFSDBFile doc = staging_repository.findDOByID(ID);
			DBObject doc_metadata = doc.getMetaData();
			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(true, JSON.serialize(doc_metadata));
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}

	}

	@GET
	@Path("/find/data")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public void DOfindData(@QueryParam("ID") String ID, @Context HttpServletResponse response)
						   {
		// Connect to MongoDB and return DO data files as response
		// return
		if (ID == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			GridFSDBFile doc = staging_repository.findDOByID(ID);
			response.setContentType(doc.getContentType());
			response.setContentLengthLong(doc.getLength());
			response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFilename() + "\"");
			OutputStream out = response.getOutputStream();
			doc.writeTo(out);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageListResponse DOlist() {
		// Connect to mongoDB and list all DOs in staging DB
		// return list of DO ids
		try {
			List<GridFSDBFile> DO_list = staging_repository.listAll();
			List<String> DO_id_list = new ArrayList<String>();
			for (GridFSDBFile DO : DO_list) {
				String id = DO.getId().toString();
				DO_id_list.add(id);
			}

			MessageListResponse response = new MessageListResponse(true, DO_id_list);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageListResponse response = new MessageListResponse(false, null);
			return response;
		}
	}

	@PUT
	@Path("/update/metadata")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse setMetadata(@QueryParam("ID") String id, String metadata) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID
		if (id == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			// Get original Grid FS file
			GridFSDBFile doc = staging_repository.findDOByID(id);

			// Form updated metadata object
			DBObject metadataObject = (DBObject) JSON.parse(metadata);
			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = staging_repository.addDO(doc.getInputStream(), doc.getFilename(), doc.getContentType(),
					metadataObject);

			MessageResponse response = new MessageResponse(true, updated_id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@PUT
	@Path("/update/data")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public MessageResponse setData(@QueryParam("ID") String id, FormDataContentDisposition file,
								   InputStream inputStream) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID
		if (id == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			// Get original Grid FS file
			GridFSDBFile doc = staging_repository.findDOByID(id);

			// Ingest multipart file into inputstream
			String file_name = file.getFileName();
			String content_type = file.getType();

			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = staging_repository.addDO(inputStream, file_name, content_type, doc.getMetaData());
			MessageResponse response = new MessageResponse(true, updated_id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@DELETE
	@Path("/delete")
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOdelete(@QueryParam("ID") String ID) {
		// Connect to MongoDB and delete DO
		// Return true or false
		if (ID == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			boolean status = staging_repository.deleteDOByID(ID);

			// Convert Json Node to message response type
			MessageResponse response = new MessageResponse(status, null);
			return response;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	// After DO upload and staging step, user finally add DO to permanent
	// repository
	// (ADD operation)
	// During ADD operation, DO will be copied to permanent repository database
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MessageResponse DOadd(@QueryParam("ID") String id) {

		// Connect to Mongo staging DB and get DO information
		if (id == null) {
			throw new WebApplicationException(
					Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
							.entity("ID parameter is mandatory")
							.build()
			);
		}
		try {
			if (staging_repository.existDOByID(id)) {
				GridFSDBFile doc = staging_repository.findDOByID(id);

				// Transfer DO from staging database to permanent repository
				// DO in repo can create and read, but update and delete
				// operation is disallowed.
				String repo_id = permanent_repository.addDO(doc.getInputStream(), doc.getFilename(),
						doc.getContentType(), doc.getMetaData());

				// Return message response with registered PID record
				MessageResponse response = new MessageResponse(true, repo_id);
				return response;
			} else {
				MessageResponse response = new MessageResponse(false, null);
				return response;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}
}