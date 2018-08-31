/*
 *
 * Copyright 2018 The Trustees of Indiana University
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
 *
 * @creator quzhou@umail.iu.edu
 * @rewritten by kunarath@iu.edu
 */
package data.repository.pragma;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import data.repository.pragma.mongo.PermanentRepository;
import data.repository.pragma.mongo.StagingDBRepository;
import data.repository.pragma.response.MessageListResponse;
import data.repository.pragma.response.MessageResponse;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;

/**
 * Handles requests for the occurrence set upload and query
 * 
 */
@Path("/")
public class DataObjectController {


//	private StagingDBRepository staging_repository = new StagingDBRepository();
//
//	private PermanentRepository permanent_repository = new PermanentRepository();
//
//	@Context
//	private HttpServletResponse http_response = new HttpServletResponse() {
//		@Override
//		public void addCookie(Cookie cookie) {
//
//		}
//
//		@Override
//		public boolean containsHeader(String s) {
//			return false;
//		}
//
//		@Override
//		public String encodeURL(String s) {
//			return null;
//		}
//
//		@Override
//		public String encodeRedirectURL(String s) {
//			return null;
//		}
//
//		@Override
//		public String encodeUrl(String s) {
//			return null;
//		}
//
//		@Override
//		public String encodeRedirectUrl(String s) {
//			return null;
//		}
//
//		@Override
//		public void sendError(int i, String s) throws IOException {
//
//		}
//
//		@Override
//		public void sendError(int i) throws IOException {
//
//		}
//
//		@Override
//		public void sendRedirect(String s) throws IOException {
//
//		}
//
//		@Override
//		public void setDateHeader(String s, long l) {
//
//		}
//
//		@Override
//		public void addDateHeader(String s, long l) {
//
//		}
//
//		@Override
//		public void setHeader(String s, String s1) {
//
//		}
//
//		@Override
//		public void addHeader(String s, String s1) {
//
//		}
//
//		@Override
//		public void setIntHeader(String s, int i) {
//
//		}
//
//		@Override
//		public void addIntHeader(String s, int i) {
//
//		}
//
//		@Override
//		public void setStatus(int i) {
//
//		}
//
//		@Override
//		public void setStatus(int i, String s) {
//
//		}
//
//		@Override
//		public int getStatus() {
//			return 0;
//		}
//
//		@Override
//		public String getHeader(String s) {
//			return null;
//		}
//
//		@Override
//		public Collection<String> getHeaders(String s) {
//			return null;
//		}
//
//		@Override
//		public Collection<String> getHeaderNames() {
//			return null;
//		}
//
//		@Override
//		public String getCharacterEncoding() {
//			return null;
//		}
//
//		@Override
//		public String getContentType() {
//			return null;
//		}
//
//		@Override
//		public ServletOutputStream getOutputStream() throws IOException {
//			return null;
//		}
//
//		@Override
//		public PrintWriter getWriter() throws IOException {
//			return null;
//		}
//
//		@Override
//		public void setCharacterEncoding(String s) {
//
//		}
//
//		@Override
//		public void setContentLength(int i) {
//
//		}
//
//		@Override
//		public void setContentLengthLong(long l) {
//
//		}
//
//		@Override
//		public void setContentType(String s) {
//
//		}
//
//		@Override
//		public void setBufferSize(int i) {
//
//		}
//
//		@Override
//		public int getBufferSize() {
//			return 0;
//		}
//
//		@Override
//		public void flushBuffer() throws IOException {
//
//		}
//
//		@Override
//		public void resetBuffer() {
//
//		}
//
//		@Override
//		public boolean isCommitted() {
//			return false;
//		}
//
//		@Override
//		public void reset() {
//
//		}
//
//		@Override
//		public void setLocale(Locale locale) {
//
//		}
//
//		@Override
//		public Locale getLocale() {
//			return null;
//		}
//	} ;

	private CacheControl control = new CacheControl();

	public DataObjectController() {
		control.setNoCache(true);
	}

	@GET
	@Path("/hello")
	public Response getMsg(@PathParam("hello") String msg) {

		String output = "Jersey say : " + msg;

		return Response.status(200).entity(output).build();

	}

//	@POST
//	@Path("/DO/upload")
//	@Consumes({MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA})
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse DOupload(@FormDataParam("data") FormDataContentDisposition file,
//									@FormDataParam("data") InputStream inputStream,
//									@QueryParam("metadata") String metadata) {
//		try {
//			// Create metadata DBObject from input
//			DBObject metadataObject = (DBObject) JSON.parse(metadata);
//
//			// Ingest multipart file into inputstream;
//			String file_name = file.getFileName();
//			String content_type = file.getType();
//			// Connect to MongoDB and use GridFS to store metadata and data
//			// Return created DO internal id in stagingDB
//			String id = staging_repository.addDO(inputStream, file_name, content_type, metadataObject);
//			MessageResponse response = new MessageResponse(true, id);
//			return response;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
//	}
//
//	@GET
//	@Path("/DO/find/metadata")
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse DOfindMedata(@QueryParam("ID") String ID) {
//		// Connect to MongoDB and return DO metadata as response
//		// return
//		try {
//			GridFSDBFile doc = staging_repository.findDOByID(ID);
//			DBObject doc_metadata = doc.getMetaData();
//			// Convert Json Node to message response type
//			MessageResponse response = new MessageResponse(true, JSON.serialize(doc_metadata));
//			return response;
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
//
//	}
//
//	@GET
//	@Path("/DO/find/data")
//	@Produces(MediaType.APPLICATION_JSON)
//	public void DOfindData(@QueryParam("ID") String ID) {
//		// Connect to MongoDB and return DO data files as response
//		// return
//		try {
//			GridFSDBFile doc = staging_repository.findDOByID(ID);
//			http_response.setContentType(doc.getContentType());
//			http_response.setContentLengthLong(doc.getLength());
//			http_response.setHeader("Content-Disposition", "attachment; filename=\"" + doc.getFilename() + "\"");
//			OutputStream out = http_response.getOutputStream();
//			doc.writeTo(out);
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@GET
//	@Path("/DO/list")
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageListResponse DOlist() {
//		// Connect to mongoDB and list all DOs in staging DB
//		// return list of DO ids
//		try {
//			List<GridFSDBFile> DO_list = staging_repository.listAll();
//			List<String> DO_id_list = new ArrayList<String>();
//			for (GridFSDBFile DO : DO_list) {
//				String id = DO.getId().toString();
//				DO_id_list.add(id);
//			}
//
//			MessageListResponse response = new MessageListResponse(true, DO_id_list);
//			return response;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			MessageListResponse response = new MessageListResponse(false, null);
//			return response;
//		}
//	}
//
//	@GET
//	@Path("/DO/update/metadata")
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse setMetadata(@QueryParam("ID") String id, String metadata) {
//		// Connect to MongoDB and set metadata as curation purposes
//		// Return a new document ID
//
//		try {
//			// Get original Grid FS file
//			GridFSDBFile doc = staging_repository.findDOByID(id);
//
//			// Form updated metadata object
//			DBObject metadataObject = (DBObject) JSON.parse(metadata);
//			// Push back updated DO to MongoDB and get a new doc ID
//			String updated_id = staging_repository.addDO(doc.getInputStream(), doc.getFilename(), doc.getContentType(),
//					metadataObject);
//			MessageResponse response = new MessageResponse(true, updated_id);
//			return response;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
//	}
//
//	@GET
//	@Path("/DO/update/data")
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse setData(@QueryParam("ID") String id,
//								   @FormDataParam("data") FormDataContentDisposition file,
//								   @FormDataParam("data") InputStream inputStream) {
//		// Connect to MongoDB and set metadata as curation purposes
//		// Return a new document ID
//
//		try {
//			// Get original Grid FS file
//			GridFSDBFile doc = staging_repository.findDOByID(id);
//
//			// Ingest multipart file into inputstream
//			String file_name = file.getFileName();
//			String content_type = file.getType();
//
//			// Push back updated DO to MongoDB and get a new doc ID
//			String updated_id = staging_repository.addDO(inputStream, file_name, content_type, doc.getMetaData());
//			MessageResponse response = new MessageResponse(true, updated_id);
//			return response;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
//	}
//
//	@GET
//	@Path("/DO/delete")
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse DOdelete(@QueryParam("ID") String ID) {
//		// Connect to MongoDB and delete DO
//		// Return true or false
//		try {
//			boolean status = staging_repository.deleteDOByID(ID);
//
//			// Convert Json Node to message response type
//			MessageResponse response = new MessageResponse(status, null);
//			return response;
//
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
//	}
//
//	// After DO upload and staging step, user finally add DO to permanent
//	// repository
//	// (ADD operation)
//	// During ADD operation, DO will be copied to permanent repository database
//	@POST
//	@Path("/DO/add")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.APPLICATION_JSON)
//	public MessageResponse DOadd(@QueryParam("ID") String id) {
//
//		// Connect to Mongo staging DB and get DO information
//
//		try {
//			if (staging_repository.existDOByID(id)) {
//				GridFSDBFile doc = staging_repository.findDOByID(id);
//
//				// Transfer DO from staging database to permanent repository
//				// DO in repo can create and read, but update and delete
//				// operation is disallowed.
//				String repo_id = permanent_repository.addDO(doc.getInputStream(), doc.getFilename(),
//						doc.getContentType(), doc.getMetaData());
//
//				// Return message response with registered PID record
//				MessageResponse response = new MessageResponse(true, repo_id);
//				return response;
//			} else {
//				MessageResponse response = new MessageResponse(false, null);
//				return response;
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			System.out.println(e.toString());
//			MessageResponse response = new MessageResponse(false, null);
//			return response;
//		}
////	}
}
