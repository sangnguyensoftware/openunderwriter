package com.ail.service.policy;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ail.pageflow.service.RestfulServiceBridge;

@Path("/")
public class PolicyService {
    @GET
    @Path("{policyId}/{command}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("policyId") String policyId, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).policyId(policyId).invoke();
    }

    @POST
    @Path("{policyId}/{command}")
    @Produces(APPLICATION_JSON)
    public Response post(@PathParam("policyId") String policyId, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config, String data) {
        return new RestfulServiceBridge(command, request, response, config).policyId(policyId).jsonData(data).invoke();
    }

    @POST
    @Path("{policyId}/{command}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Response postMultipart(@PathParam("policyId") String policyId, @PathParam("command") String command, MultipartFormDataInput parts, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).policyId(policyId).multiparts(parts).invoke();
    }
}