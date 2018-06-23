package com.ail.service.pageflow;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ail.core.CoreProxy;
import com.ail.core.product.liferay.LiferayActionPermissionsHelper;
import com.ail.core.product.liferay.LiferayResourceActionPermission;
import com.ail.pageflow.PageElement;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;

@Path("/permissions/")
public class PageFlowPermissionsService {

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("{permissionName}/createPermission")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response createPermission(@PathParam("permissionName") String permissionName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            LiferayActionPermissionsHelper.addAction(PageElement.class.getName(), permissionName);

            return Response.status(SC_OK).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("{permissionName: .*}/pageflow/{pageFlowName}/product/{productName}/page/{pageName}/setPermissions")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response setPermissions(@PathParam("permissionName") String permissionName,
                                    @PathParam("pageFlowName") String pageFlowName,
                                    @PathParam("productName") String productName,
                                    @PathParam("pageName") String pageName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            PageFlow pageFlow = PageFlowContext.getCoreProxy().newProductType(productName, pageFlowName, PageFlow.class);
            for (PageElement pageElement : pageFlow.getPages()) {
                if (pageElement.getId().equals(pageName)) {
                    pageElement.setPermissions(permissionName);
                }
            }

            return Response.status(SC_OK).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("pageflow/{pageFlowName}/product/{productName}/page/{pageName}/getPermissions")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response getPermissions(@PathParam("pageFlowName") String pageFlowName,
                                    @PathParam("productName") String productName,
                                    @PathParam("pageName") String pageName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            String permissions = "";
            PageFlow pageFlow = PageFlowContext.getCoreProxy().newProductType(productName, pageFlowName, PageFlow.class);
            for (PageElement pageElement : pageFlow.getPages()) {
                if (pageElement.getId().equals(pageName)) {
                    permissions = pageElement.getPermissions();
                    break;
                }
            }

            return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) permissions)).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("{permissionName}/role/{roleName}/givePermission")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response givePermission(@PathParam("permissionName") String permissionName,
                                    @PathParam("roleName") String roleName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            LiferayActionPermissionsHelper.givePermission(PageElement.class.getName(), permissionName, roleName);

            return Response.status(SC_OK).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("{permissionName}/role/{roleName}/removePermission")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response removePermission(@PathParam("permissionName") String permissionName,
                                    @PathParam("roleName") String roleName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            LiferayActionPermissionsHelper.removePermission(PageElement.class.getName(), permissionName, roleName);

            return Response.status(SC_OK).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("role/{roleName}/getPermissions")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response getPermissions(@PathParam("roleName") String roleName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            List<LiferayResourceActionPermission> permissions = LiferayActionPermissionsHelper.getPermissions(roleName);

            return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) permissions)).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @POST
    @RolesAllowed({ "Administrator" })
    @Path("{permissionName}/role/{roleName}/hasPermission")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response hasPermission(@PathParam("permissionName") String permissionName,
                                    @PathParam("roleName") String roleName) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            Boolean hasPermission = Boolean.valueOf(LiferayActionPermissionsHelper.hasPermission(PageElement.class.getName(), permissionName, roleName));

            return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) hasPermission)).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }
}