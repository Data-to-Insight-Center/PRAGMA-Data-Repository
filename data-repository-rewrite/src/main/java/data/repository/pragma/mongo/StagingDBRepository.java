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

package data.repository.pragma.mongo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.gridfs.GridFSDBFile;
import data.repository.pragma.utils.MongoDB;

public class StagingDBRepository {

	MongoDatabase db = MongoDB.getStagingDatabase();
	GridFS file_store = new GridFS((DB) db, "fs");

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
		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		GridFSDBFile gridFile = file_store.findOne(query);
		return gridFile;
	}

	public boolean deleteDOByID(String id) {
		BasicDBObject delete_query = new BasicDBObject();
		delete_query.put("_id", id);
		if (file_store.findOne(delete_query).equals(null)){
			return false;
		}else{
			file_store.remove(delete_query);
			return true;
		}
	}

	public boolean existDOByID(String id) {
		BasicDBObject exist_query = new BasicDBObject();
		exist_query.put("_id", id);
		boolean result = file_store.findOne(exist_query).equals(null);
		return !result;
	}
}