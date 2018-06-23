package com.ail.service.integrator;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.document.Document;
import com.ail.core.product.ListProductsService.ListProductsCommand;
import com.ail.core.product.ProductDetails;
import com.ail.core.security.ConfirmObjectAccessibilityToUserService.ConfirmObjectAccessibilityToUserCommand;
import com.ail.core.transformer.TransformerException;
import com.ail.core.transformer.Transformers;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.PolicySearchService.PolicySearchCommand;
import com.ail.party.search.PartySearchService.PartySearchCommand;
import com.ail.service.integrator.model.PolicySummary;
import com.ail.service.integrator.transformer.PolicySummaryTransformer;

@Path("/")
public class Integrator {
    @DELETE
    @RolesAllowed({ "Integrator" })
    @Path("policy/{uid}")
    public Response delete(@PathParam("uid") Long uid, @DefaultValue("false") @QueryParam("physical") String physical, @Context HttpServletRequest request) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            Policy policy = (Policy) coreProxy.queryUnique("get.policy.by.systemId", uid);

            if (policy == null) {
                return Response.status(SC_BAD_REQUEST).entity("Policy does not exist (uid: "+uid+")").build();
            }

            if (Boolean.parseBoolean(physical)) {
                coreProxy.delete(policy);
            }
            else {
                policy.setStatus(PolicyStatus.DELETED);
            }

            return Response.status(SC_OK).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @RolesAllowed({ "Integrator" })
    @Path("policy/{uid}")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response policy(@PathParam("uid") Long uid, @Context HttpServletRequest request) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            Policy policy = (Policy) coreProxy.queryUnique("get.policy.by.systemId", uid);

            if (policy != null) {
                String requestType = request.getContentType() != null ? request.getContentType() : APPLICATION_JSON;

                if (!userHasAccessTo(coreProxy, CoreContext.getRemoteUser(), policy)) {
                    return Response.status(SC_FORBIDDEN).build();
                }

                switch (requestType) {
                case APPLICATION_JSON:
                    return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) policy)).build();
                case APPLICATION_XML:
                    return Response.status(SC_OK).type(APPLICATION_XML).entity((Object) coreProxy.toXML((Object) policy).toString()).build();
                default:
                    return Response.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
                }
            }

            return Response.status(SC_NOT_FOUND).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean userHasAccessTo(CoreProxy coreProxy, Long userId, Policy policy) throws BaseException {
        ConfirmObjectAccessibilityToUserCommand ca = coreProxy.newCommand("ConfirmObjectAccessibilityToUserCommand", ConfirmObjectAccessibilityToUserCommand.class);
        ca.setObjectArg(policy);
        ca.setUserIdArg(userId);
        ca.invoke();
        return ca.getWriteAccessRet();
    }

    @GET
    @RolesAllowed({ "Integrator" })
    @Path("policies")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response policies(@DefaultValue("0") @QueryParam("updatedDateSince") Long updatedSince,
                             @DefaultValue("0") @QueryParam("quoteDateSince") Long quoteDateSince,
                             @QueryParam("quoteDateTo") Long quoteDateTo,
                             @DefaultValue("") @QueryParam("policyNumber") String policyNumber,
                             @DefaultValue("") @QueryParam("quotationNumber") String quotationNumber,
                             @DefaultValue("") @QueryParam("product") String product,
                             @DefaultValue("") @QueryParam("status") String status,
                             @DefaultValue("false") @QueryParam("includeTestCases") String includeTestCases,
                             @QueryParam("createdDateFrom") Long createdDateFrom,
                             @QueryParam("createdDateTo") Long createdDateTo,
                             @QueryParam("inceptionDateFrom") Long inceptionDateFrom,
                             @QueryParam("inceptionDateTo") Long inceptionDateTo,
                             @DefaultValue("Created Date DESC") @QueryParam("orderBy") String orderBy,
                             @Context HttpServletRequest request,
                             @Context SecurityContext sc) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            PolicySearchCommand psc=coreProxy.newCommand(PolicySearchCommand.class);
            psc.setUpdatedDateArg(new Date(updatedSince));
            psc.setQuoteDateMinimumArg(new Date(quoteDateSince));
            if (quoteDateTo != null) {
                psc.setQuoteDateMaximumArg(new Date(quoteDateTo));
            }
            psc.setPolicyNumberArg(policyNumber);
            psc.setQuotationNumberArg(quotationNumber);
            psc.setProductTypeIdArg(product);
            if (status != null && status.length()!=0) {
                List<PolicyStatus> policyStatuses = new ArrayList<>();
                // Allow for comma separated list of statuses
                for (StringTokenizer st = new StringTokenizer(status, ","); st.hasMoreTokens(); ) {
                    policyStatuses.add(PolicyStatus.valueOf(st.nextToken()));
                }
                psc.setPolicyStatusArg(policyStatuses);
            }
            psc.setIncludeTestArg(Boolean.parseBoolean(includeTestCases));
            if (createdDateFrom != null) {
                psc.setCreatedDateMinimumArg(new Date(createdDateFrom));
            }
            if (createdDateTo != null) {
                psc.setCreatedDateMaximumArg(new Date(createdDateTo));
            }
            if (inceptionDateFrom != null) {
                psc.setInceptionDateMinimumArg(new Date(inceptionDateFrom));
            }
            if (inceptionDateTo != null) {
                psc.setInceptionDateMaximumArg(new Date(inceptionDateTo));
            }
            psc.setOrderByArg(orderBy);
            psc.setUserIdArg(CoreContext.getRemoteUser());
            psc.invoke();

            List<PolicySummary> results = new Transformers<Policy, PolicySummary>().transform(
                    psc.getPoliciesRet(), new PolicySummaryTransformer());

            if (results != null && results.size()!=0) {
                String requestType = request.getContentType() != null ? request.getContentType() : APPLICATION_JSON;

                switch (requestType) {
                case APPLICATION_JSON:
                    return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) results)).build();
                case APPLICATION_XML:
                    return Response.status(SC_OK).type(APPLICATION_XML).entity((Object) coreProxy.toXML((Object) results).toString()).build();
                default:
                    return Response.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
                }
            }

            return Response.status(SC_NOT_FOUND).build();
        } catch (BaseException | TransformerException e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @RolesAllowed({ "Integrator" })
    @Path("document/{uid}")
    @Produces("application/pdf")
    public Response document(@PathParam("uid") Long uid, @Context HttpServletRequest request) {
        CoreProxy coreProxy = new CoreProxy();

        if (request.getContentType()!=null && !"application/pdf".equals(request.getContentType())) {
            return Response.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
        }

        try {
            Document document = (Document) coreProxy.queryUnique("get.document.by.systemId", uid);

            if (document != null) {
                return Response.status(SC_OK).entity(document.getDocumentContent()).build();
            }

            return Response.status(SC_NOT_FOUND).build();
        } catch (Throwable e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @RolesAllowed({ "Integrator" })
    @Path("products")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response products(@Context HttpServletRequest request) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            ListProductsCommand lpc = coreProxy.newCommand(ListProductsCommand.class);
            lpc.invoke();
            Collection<ProductDetails> products = lpc.getProductsRet();

            if (products != null) {
                String requestType = request.getContentType() != null ? request.getContentType() : APPLICATION_JSON;

                switch (requestType) {
                case APPLICATION_JSON:
                    return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) products)).build();
                case APPLICATION_XML:
                    return Response.status(SC_OK).type(APPLICATION_XML).entity((Object) coreProxy.toXML((Object) products).toString()).build();
                default:
                    return Response.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
                }
            }

            return Response.status(SC_NOT_FOUND).build();
        } catch (BaseException e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @RolesAllowed({ "Integrator" })
    @Path("parties")
    @Produces({ APPLICATION_JSON, APPLICATION_XML })
    public Response parties(@DefaultValue("") @QueryParam("legalName") String legalName,
                            @DefaultValue("") @QueryParam("emailAddress") String emailAddress,
                            @DefaultValue("") @QueryParam("addressLine") String addressLine,
                            @DefaultValue("") @QueryParam("postcode") String postcode,
                            @DefaultValue("") @QueryParam("mobilephoneNumber") String mobilephoneNumber,
                            @DefaultValue("") @QueryParam("telephoneNumber") String telephoneNumber,
                            @Context HttpServletRequest request,
                            @Context SecurityContext sc) {
        CoreProxy coreProxy = new CoreProxy();

        try {
            PartySearchCommand psc=coreProxy.newCommand(PartySearchCommand.class);
            psc.setLegalNameArg(legalName);
            psc.setEmailAddressArg(emailAddress);
            psc.setMobilephoneNumberArg(mobilephoneNumber);
            psc.setPostcodeArg(postcode);
            psc.setTelephoneNumberArg(telephoneNumber);
            psc.setAddressLineArg(addressLine);
            psc.setUserIdArg(CoreContext.getRemoteUser());

            psc.invoke();

            if (psc.getPartyRet() != null && psc.getPartyRet().size()!=0) {
                String requestType = request.getContentType() != null ? request.getContentType() : APPLICATION_JSON;

                switch (requestType) {
                case APPLICATION_JSON:
                    return Response.status(SC_OK).type(APPLICATION_JSON).entity((Object) coreProxy.toJSON((Object) psc.getPartyRet())).build();
                case APPLICATION_XML:
                    return Response.status(SC_OK).type(APPLICATION_XML).entity((Object) coreProxy.toXML((Object) psc.getPartyRet()).toString()).build();
                default:
                    return Response.status(SC_UNSUPPORTED_MEDIA_TYPE).build();
                }
            }

            return Response.status(SC_NOT_FOUND).build();
        } catch (BaseException e) {
            coreProxy.logError(e.toString());
            return Response.status(SC_INTERNAL_SERVER_ERROR).build();
        }
    }
}