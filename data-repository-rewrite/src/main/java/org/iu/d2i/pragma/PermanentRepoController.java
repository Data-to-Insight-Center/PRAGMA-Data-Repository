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

package org.iu.d2i.pragma;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.util.JSON;

import org.iu.d2i.pragma.mongo.PermanentRepository;
import org.iu.d2i.pragma.response.MessageListResponse;
import org.iu.d2i.pragma.response.MessageResponse;

@Path("/repo")
public class PermanentRepoController {

    private PermanentRepository permanent_repository = new PermanentRepository();

    // DOs in Permanent Repo can only be read and listed;
    // Do not support update and delete

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
            GridFSDBFile doc = permanent_repository.findDOByID(ID);
            DBObject doc_metadata = doc.getMetaData();
            // Convert DBObject to message response type
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
    @Produces(MediaType.APPLICATION_JSON)
    public void DOfindData(@QueryParam("ID") String ID, @Context HttpServletResponse response) {
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
            GridFSDBFile doc = permanent_repository.findDOByID(ID);
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
    @Path("/find/DO")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageResponse DOfindAll(@QueryParam("ID") String ID) {
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
            GridFSDBFile doc = permanent_repository.findDOByID(ID);
            MessageResponse response = new MessageResponse(true, doc.toString());
            return response;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            MessageResponse response = new MessageResponse(false, null);
            return response;
        }
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public MessageListResponse DOlist() {
        // Connect to mongoDB and list all DOs in staging DB
        // return list of DO ids
        try {
            List<GridFSDBFile> DO_list = permanent_repository.listAll();
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

}
