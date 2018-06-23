package com.ail.core;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

import java.lang.reflect.Method;

import com.ail.core.jsonmapping.FromJSONService.FromJSONCommand;

public class RestfulServiceInvoker {
    public void invoke() throws BaseException {
        invoke(null);
    }

    public void invoke(Class<? extends Object> arg) throws BaseException {
        try {
            RestfulServiceReturn ret = null;

            int argCount = (arg != null
                    && CoreContext.getRestfulRequestData() != null
                    && !CoreContext.getRestfulRequestData().isEmpty()) ? 1 : 0;

            Method serviceMethod = findServiceMethod(argCount);

            if (argCount == 1) {
                FromJSONCommand fjc = CoreContext.getCoreProxy().newCommand(FromJSONCommand.class);
                fjc.setClassArg(arg);
                fjc.setJSONArg(CoreContext.getRestfulRequestData());
                fjc.invoke();
                Object args = fjc.getObjectRet();

                ret = (RestfulServiceReturn) serviceMethod.invoke(this, args);
            } else {
                ret = (RestfulServiceReturn) serviceMethod.invoke(this);
            }

            CoreContext.setRestfulResponse(ret);
        } catch (JSONException e) {
            CoreContext.setRestfulResponse(new ClientError(HTTP_BAD_REQUEST, e.getCause().getMessage(), e));
        } catch (Throwable t) {
            throw new PostconditionException("Failed to execute restful command service", t);
        }
    }

    private Method findServiceMethod(int argCount) throws BaseException {
        Method executeMethod = null;

        for (Method method : this.getClass().getDeclaredMethods()) {
            if ("service".equals(method.getName())) {
                if (argCount == method.getParameterTypes().length) {
                    executeMethod = method;
                    break;
                }
            }
        }

        if (executeMethod == null) {
            throw new PreconditionException("Service class: " + this.getClass().getName() + " extends RestfulServiceInvoker but does not define a service method.");
        }

        return executeMethod;
    }

    public static class ClientError extends RestfulServiceReturn {
        public String errorCode;

        public ClientError(int status, String errorCode) {
            this(status, errorCode, null);
        }

        public ClientError(int status, String errorCode, Throwable e) {
            this(status, errorCode, "", e);
        }

        public ClientError(int status, String errorCode, String id, Throwable e) {
            super(status);
            this.errorCode = errorCode;

            if (e != null) {
                CoreContext.getCoreProxy().logError("Service call exception: " + e.getMessage()
                    + "\n: status: " + status
                    + "\n: id: " + id
                    + "\n: msg: " + errorCode + "\n", e);
            }
        }
    }
}
