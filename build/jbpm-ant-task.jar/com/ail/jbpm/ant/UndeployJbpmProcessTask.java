package com.ail.jbpm.ant;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tools.ant.Task;

public class UndeployJbpmProcessTask extends Task {

    private String groupId;
    private String artifactId;
    private String version;

    private String protocol = "http";
    private String hostname = "localhost";
    private int port = 8080;
    private String username = "admin";
    private String password = "admin";
    
    private boolean clearHistory = false;

    @Override
    public void execute() {
        undeployProcess();
    }

    private void undeployProcess() {
        String deploymentId = groupId + ":" + artifactId + ":" + version;
        
        if (clearHistory) {
            String resource = "/jbpm-console/rest/history/clear";
            callServer(resource, "application/xml", "Clear all history, tasks, processes");
        }
        
        String resource = "/jbpm-console/rest/deployment/" + deploymentId + "/undeploy";
        callServer(resource, "application/xml", "Undeploy jbpm artifact");
    }

    private void callServer(String resource, String contentType, String description) {
        System.out.println(description + ": " + hostname + ":" + port + resource);

        HttpHost target = new HttpHost(hostname, port, protocol);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials(username, password));
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        HttpPost httpPost = new HttpPost(resource);
        if (contentType != null) {
            httpPost.setHeader("Content-Type", contentType);
        }
        AuthCache authCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(target, basicAuth);
        HttpClientContext localContext = HttpClientContext.create();
        localContext.setAuthCache(authCache);

        try {
            CloseableHttpResponse response = httpclient.execute(target, httpPost, localContext);
            System.out.println(description + " response: " + response.toString());
        } catch (ClientProtocolException e) {
            System.out.println(description + " Error: Protocol Error: " + e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(description + " Error: IOException while getting response: " + e);
            throw new RuntimeException(e);
        }
    }


    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setClearHistory(boolean clearHistory) {
        this.clearHistory = clearHistory;
    }

}
