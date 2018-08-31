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
 * @creator kunarath@iu.edu
 */
package data.repository.pragma.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constants {

    public static String mongoHost;
    public static int mongoPort;

    public static String stagingDbName;
    public static String permanentDbName;

    static {
        try {
            loadConfigurations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfigurations() throws IOException {
        InputStream inputStream = Constants.class
                .getResourceAsStream("./default.properties");
        Properties props = new Properties();
        props.load(inputStream);
        mongoHost = props.getProperty("mongo.host", "localhost");
        mongoPort = Integer.parseInt(props.getProperty("mongo.port", "27017"));
        stagingDbName = props.getProperty("staging.db.name", "PRAGMA-StagingDB");
        permanentDbName = props.getProperty("permanent.db.name", "PRAGMA-PermanentRepo");
    }
}

