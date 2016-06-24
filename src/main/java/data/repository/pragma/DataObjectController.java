/**
 * Copyright [2014-2016] PRAGMA, AIST, Data To Insight Center (IUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * @Author: Quan(Gabriel) Zhou
 */

package data.repository.pragma;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;

import data.repository.pragma.mongo.PermanentRepository;
import data.repository.pragma.mongo.StagingDBRepository;
import data.repository.pragma.response.MessageListResponse;
import data.repository.pragma.response.MessageResponse;

/**
 * Handles requests for the occurrence set upload and query
 * 
 */
@RestController
public class DataObjectController {
	@Autowired
	private StagingDBRepository Staging_repository;

	@Autowired
	private PermanentRepository permanent_repository;

	@RequestMapping(value = "/DO/upload", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOupload(@RequestParam(value = "data", required = true) MultipartFile file,
			@RequestBody String metadata) {
		try {
			// Create metadata DBObject from input
			DBObject metadataObject = (DBObject) JSON.parse(metadata);

			// Ingest multipart file into inputstream
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			String file_name = file.getOriginalFilename();
			String content_type = file.getContentType();
			// Connect to MongoDB and use GridFS to store metadata and data
			// Return created DO internal id in stagingDB
			String id = Staging_repository.addDO(inputStream, file_name, content_type, metadataObject);
			MessageResponse response = new MessageResponse(true, id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/find/metadata")
	@ResponseBody
	public MessageResponse DOfindMedata(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to MongoDB and return DO metadata as response
		// return
		try {
			GridFSDBFile doc = Staging_repository.findDOByID(ID);
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

	@RequestMapping(value = "/DO/find/data", method = RequestMethod.GET)
	@ResponseBody
	public void DOfindData(@RequestParam(value = "ID", required = true) String ID, HttpServletResponse response) {
		// Connect to MongoDB and return DO data files as response
		// return
		try {
			GridFSDBFile doc = Staging_repository.findDOByID(ID);
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

	@RequestMapping("/DO/list")
	@ResponseBody
	public MessageListResponse DOlist() {
		// Connect to mongoDB and list all DOs in staging DB
		// return list of DO ids
		try {
			List<GridFSDBFile> DO_list = Staging_repository.listAll();
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

	@RequestMapping("/DO/update/metadata")
	@ResponseBody
	public MessageResponse setMetadata(@RequestParam(value = "ID", required = true) String id,
			@RequestBody String metadata) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID

		try {
			// Get original Grid FS file
			GridFSDBFile doc = Staging_repository.findDOByID(id);

			// Form updated metadata object
			DBObject metadataObject = (DBObject) JSON.parse(metadata);
			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = Staging_repository.addDO(doc.getInputStream(), doc.getFilename(), doc.getContentType(),
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

	@RequestMapping("/DO/update/data")
	@ResponseBody
	public MessageResponse setData(@RequestParam(value = "ID", required = true) String id,
			@RequestParam(value = "data", required = true) MultipartFile file) {
		// Connect to MongoDB and set metadata as curation purposes
		// Return a new document ID

		try {
			// Get original Grid FS file
			GridFSDBFile doc = Staging_repository.findDOByID(id);

			// Ingest multipart file into inputstream
			byte[] byteArr = file.getBytes();
			InputStream inputStream = new ByteArrayInputStream(byteArr);
			String file_name = file.getOriginalFilename();
			String content_type = file.getContentType();

			// Push back updated DO to MongoDB and get a new doc ID
			String updated_id = Staging_repository.addDO(inputStream, file_name, content_type, doc.getMetaData());
			MessageResponse response = new MessageResponse(true, updated_id);
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			MessageResponse response = new MessageResponse(false, null);
			return response;
		}
	}

	@RequestMapping("/DO/delete")
	@ResponseBody
	public MessageResponse DOdelete(@RequestParam(value = "ID", required = true) String ID) {
		// Connect to MongoDB and delete DO
		// Return true or false
		try {
			boolean status = Staging_repository.deleteDOByID(ID);

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
	@RequestMapping(value = "/DO/add", method = RequestMethod.POST)
	@ResponseBody
	public MessageResponse DOadd(@RequestParam(value = "ID", required = true) String id) {

		// Connect to Mongo staging DB and get DO information

		try {
			if (Staging_repository.existDOByID(id)) {
				GridFSDBFile doc = Staging_repository.findDOByID(id);

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
