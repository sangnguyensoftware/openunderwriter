package com.ail.service.claim;

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
public class ClaimService {
    @GET
    @Path("{claimId}/{command}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("claimId") String claimId, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).claimId(claimId).invoke();
    }

    @POST
    @Path("{claimId}/{command}")
    @Produces(APPLICATION_JSON)
    public Response post(@PathParam("claimId") String claimId, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config, String data) {
        return new RestfulServiceBridge(command, request, response, config).claimId(claimId).jsonData(data).invoke();
    }

    @POST
    @Path("{claimId}/{command}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    public Response postMultipart(@PathParam("claimId") String claimId, @PathParam("command") String command, MultipartFormDataInput parts, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).claimId(claimId).multiparts(parts).invoke();
    }
}