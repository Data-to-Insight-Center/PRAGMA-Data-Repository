package org.iu.d2i.pragma.filter;

/**
 * Created by kunarath on 8/8/18.
 */
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.MultivaluedMap;

public class CORSResponseFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest,
                                    ContainerResponse containerResponse) {
        MultivaluedMap<String, Object> headers = containerResponse.getHttpHeaders();
        // add CORS header to allow accesses from other domains
        headers.add("Access-Control-Allow-Origin", "*");
        return containerResponse;
    }

}
