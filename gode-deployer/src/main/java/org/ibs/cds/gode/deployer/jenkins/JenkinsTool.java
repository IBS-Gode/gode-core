package org.ibs.cds.gode.deployer.jenkins;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.common.IntegerResponse;
import com.cdancy.jenkins.rest.domain.common.RequestStatus;
import com.cdancy.jenkins.rest.features.JobsApi;
import org.apache.commons.collections4.CollectionUtils;
import org.ibs.cds.gode.entity.generic.AB;
import org.ibs.cds.gode.status.BinaryStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JenkinsTool {

    private JenkinsClient client;
    private String templatePath;

    public JenkinsTool(String endpoint, String credentials, String templatePath){
        this.templatePath = templatePath.endsWith(File.separator) ? templatePath : templatePath.concat(File.separator);
        this.client = JenkinsClient.builder()
                .endPoint(endpoint)
                .credentials(credentials)
                .build();
    }
    
    public JenkinsTool(String endpoint, String credentials){
        this.templatePath = "pipeline".concat(File.separator).concat("jenkins").concat(File.separator);
        this.client = JenkinsClient.builder()
                .endPoint(endpoint)
                .credentials(credentials)
                .build();
    }

    public boolean createJob(String project, String template) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource resource = resolver.getResource(templatePath.concat(template.concat(".xml")));
        RequestStatus success = client.api().jobsApi().create(null,project, Files.readString(Paths.get(resource.getURI())));
       return success.value();
    }
    
    
    public BinaryStatus build(String project) {
        IntegerResponse output = client.api().jobsApi().build(null, project);
        return BinaryStatus.valueOf(output != null && output.value() != null && output.value() > 0 && CollectionUtils.isEmpty(output.errors()));
    }

    public BinaryStatus buildWithParameters(String project, Map<String, List<String>> parameters) {
        IntegerResponse output = client.api().jobsApi().buildWithParameters(null, project, parameters);
        return BinaryStatus.valueOf(output != null && output.value() != null && output.value() > 0 && CollectionUtils.isEmpty(output.errors()));
    }

    public AB<String,Integer> lastBuild(String project){
        JobsApi jobsApi = client.api().jobsApi();
        return AB.of(jobsApi.lastBuildTimestamp(null, project), jobsApi.lastBuildNumber(null, project));
    }

}
