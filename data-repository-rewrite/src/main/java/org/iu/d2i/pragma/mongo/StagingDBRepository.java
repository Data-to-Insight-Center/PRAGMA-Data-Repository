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

package org.iu.d2i.pragma.mongo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.gridfs.GridFSDBFile;

import org.bson.types.ObjectId;
import org.iu.d2i.pragma.util.Constants;

public class StagingDBRepository {

	Mongo mongo = new Mongo(Constants.mongoHost, Constants.mongoPort);
	DB db = mongo.getDB(Constants.stagingDbName);
	GridFS file_store = new GridFS(db);

	public String addDO(InputStream inputStream, String file_name, String content_type, DBObject metadata) {
		GridFSInputFile gfsFile = file_store.createFile(inputStream);
		gfsFile.put("filename", file_name);
		gfsFile.put("contentType", content_type);
		gfsFile.put("metadata", metadata);
		gfsFile.save();
		return gfsFile.getId().toString();
	}

	public List<GridFSDBFile> listAll() {
		List<GridFSDBFile> files = new ArrayList<GridFSDBFile>();
		DBCursor cursor = file_store.getFileList();
		while (cursor.hasNext()) {
			files.add((GridFSDBFile) cursor.next());
		}
		return files;
	}

	public GridFSDBFile findDOByID(String id) {
		ObjectId object_id = new ObjectId(id);
		GridFSDBFile gridFile = file_store.findOne(object_id);
		return gridFile;
	}

	public boolean deleteDOByID(String id) {
		ObjectId object_id = new ObjectId(id);
		GridFSDBFile gridFile = file_store.findOne(object_id);
		if (gridFile.getId().equals(object_id)) {
			file_store.remove(object_id);
			return true;
		}else{
			return false;
		}
	}

	public boolean existDOByID(String id) {
		ObjectId object_id = new ObjectId(id);
		GridFSDBFile gridFile = file_store.findOne(object_id);
		boolean result = gridFile.getId().equals(object_id);
		return result;
	}
}