package com.github.gpor0;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestFilter implements ContainerRequestFilter {

    private static final Logger LOG = LogManager.getLogger("FlightService");

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOG.info("API request {} {}?{}",
                requestContext.getMethod(), requestContext.getUriInfo().getPath(), requestContext.getUriInfo().getQueryParameters());
    }
}
