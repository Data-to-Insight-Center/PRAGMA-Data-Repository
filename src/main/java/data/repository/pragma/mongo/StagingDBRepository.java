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

package data.repository.pragma.mongo;

import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

@Repository
public class StagingDBRepository {

	@Autowired
	private GridFsTemplate stagingDBTemplate;

	public String addDO(InputStream inputStream, String file_name, String content_type, DBObject metadata) {
		return stagingDBTemplate.store(inputStream, file_name, content_type, metadata).getId().toString();
	}

	public List<GridFSDBFile> listAll() {
		return stagingDBTemplate.find(null);
	}

	public GridFSDBFile findDOByID(String id) {
		return stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id)));
	}

	public boolean deleteDOByID(String id) {
		if (stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id))).equals(null))
			return false;
		else {
			stagingDBTemplate.delete(Query.query(Criteria.where("_id").is(id)));
			return true;
		}
	}

	public boolean existDOByID(String id) {
		boolean result = stagingDBTemplate.findOne(Query.query(Criteria.where("_id").is(id))).equals(null);
		return !result;
	}

	public boolean existDOByKey(String key, String value) {
		boolean result = stagingDBTemplate.findOne(Query.query(Criteria.where(key).is(value))).equals(null);
		return !result;
	}

	public GridFSDBFile findDOByKey(String key, String value) {
		return stagingDBTemplate.findOne(Query.query(Criteria.where(key).is(value)));
	}

	public boolean deleteDOByKey(String key, String value) {
		if (stagingDBTemplate.findOne(Query.query(Criteria.where(key).is(value))).equals(null))
			return false;
		else {
			stagingDBTemplate.delete(Query.query(Criteria.where(key).is(value)));
			return true;
		}
	}
}
