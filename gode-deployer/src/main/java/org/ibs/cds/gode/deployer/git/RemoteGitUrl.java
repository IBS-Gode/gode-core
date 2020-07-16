package org.ibs.cds.gode.deployer.git;

import lombok.Data;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 *
 * @author manugraj
 */
@Data
public class RemoteGitUrl {

    public static final String USERNAME_PASSWD = "UP";
    public static final String TOKEN = "T";
    private final String username;
    private final String password;
    private final String token;
    private final String authType;
    private String completeUrl;

    public RemoteGitUrl(String url, String orgName, String username, String password) {
        this.username = username;
        this.password = password;
        this.authType = USERNAME_PASSWD;
        this.token = null;
    }
    
    public RemoteGitUrl(String url, String orgName, String token) {
        this.username = null;
        this.password = null;
        this.authType = TOKEN;
        this.token = token;
    }
    
    public RemoteGitUrl(String completeUrl, String token) {
        this.completeUrl = completeUrl;
        this.username = null;
        this.password = null;
        this.authType = TOKEN;
        this.token = token;
    }

    public CredentialsProvider credentials(){
        if(token == null) return new UsernamePasswordCredentialsProvider(username, password);
        return new UsernamePasswordCredentialsProvider(token, "");
    }
    
    public String remoteUrl(){
        return completeUrl;
    }
}
