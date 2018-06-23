package com.ail.service.pageflow;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ail.pageflow.service.RestfulPageFlowBridge;

@Path("/")
public class PageFlowService {
    @GET
    @POST
    @Path("{pageFlowName}/product/{productName}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response get(@PathParam("pageFlowName") String pageFlowName, @PathParam("productName") String productName) {
        return new RestfulPageFlowBridge(pageFlowName).product(productName).invoke();
    }

    @GET
    @POST
    @Path("{pageFlowName}/product/{productName}/page/{pageName}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response get(@PathParam("pageFlowName") String pageFlowName, @PathParam("productName") String productName, @PathParam("pageName") String pageName) {
        return new RestfulPageFlowBridge(pageFlowName).product(productName).page(pageName).invoke();
    }

    @GET
    @POST
    @Path("{pageFlowName}/policy/{policyId}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response post(@PathParam("pageFlowName") String pageFlowName, @PathParam("policyId") String policyId) {
        return new RestfulPageFlowBridge(pageFlowName).policyId(policyId).invoke();
    }

    @GET
    @POST
    @Path("{pageFlowName}/policy/{policyId}/page/{pageName}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response post(@PathParam("pageFlowName") String pageFlowName, @PathParam("policyId") String policyId, @PathParam("pageName") String pageName) {
        return new RestfulPageFlowBridge(pageFlowName).policyId(policyId).page(pageName).invoke();
    }
}