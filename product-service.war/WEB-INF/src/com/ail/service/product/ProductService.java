package com.ail.service.product;

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
public class ProductService {
    @GET
    @Path("{product}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response get(@PathParam("product") String product, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).invoke();
    }

    @POST
    @Path("{product}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response post(@PathParam("product") String product, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config, String data) {
        return new RestfulServiceBridge(command, request, response, config).product(product).jsonData(data).invoke();
    }

    @POST
    @Path("{product}/{command}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response postMultipart(@PathParam("product") String product, @PathParam("command") String command, MultipartFormDataInput parts, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).multiparts(parts).invoke();
    }

    @GET
    @Path("{product}/party/{party}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response partyGet(@PathParam("product") String product, @PathParam("party") String partyId, @PathParam("command") String command,
                            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).partyId(partyId).invoke();
    }

    @POST
    @Path("{product}/party/{partyId}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response partyPost(@PathParam("product") String product, @PathParam("party") String partyId, @PathParam("command") String command,
                            @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config, String data) {
        return new RestfulServiceBridge(command, request, response, config).product(product).partyId(partyId).jsonData(data).invoke();
    }

    @POST
    @Path("{product}/party/{partyId}/{command}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    public Response partyPostMultipart(@PathParam("product") String product, @PathParam("party") String partyId, @PathParam("command") String command,
                                    MultipartFormDataInput parts, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).partyId(partyId).multiparts(parts).invoke();
    }

    /**
     * @deprecated Use the GET service on the PolicyService instead.
     */
    @GET
    @Path("{product}/{policyId}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    @Deprecated
    public Response get(@PathParam("product") String product, @PathParam("policyId") String policyId, @PathParam("command") String command, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).policyId(policyId).invoke();
    }

    /**
     * @deprecated Use the POST service on the PolicyService instead.
     */
    @POST
    @Path("{product}/{policyId}/{command}")
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    @Deprecated
    public Response post(@PathParam("product") String product, @PathParam("policyId") String policyId, @PathParam("command") String command, String data, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).policyId(policyId).jsonData(data).invoke();
    }

    /**
     * @deprecated Use the postMultipart service on the PolicyService instead.
     */
    @POST
    @Path("{product}/{policyId}/{command}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON + ";" + "charset=UTF-8")
    @Deprecated
    public Response postMultipart(@PathParam("product") String product, @PathParam("policyId") String policyId, @PathParam("command") String command, MultipartFormDataInput parts, @Context HttpServletRequest request, @Context HttpServletResponse response, @Context ServletConfig config) {
        return new RestfulServiceBridge(command, request, response, config).product(product).policyId(policyId).multiparts(parts).invoke();
    }
}