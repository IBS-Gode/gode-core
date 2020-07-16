package org.ibs.cds.gode.deployer.git;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.ibs.cds.gode.exception.KnownException;

public class LocalGit {

    private final File path;
    private final Repository localRepo;
    private final Git git;
    private final String repoName;
    private RemoteGitUrl remoteGit;

    private LocalGit(String repoName, File path, RemoteGitUrl remoteGit) throws IOException {
        this.path = path;
        this.remoteGit = remoteGit;
        this.localRepo = initLocalRepo(path);
        this.git = new Git(localRepo);
        this.repoName = repoName;
    }

    public static LocalGit at(String repoName, File path, RemoteGitUrl remoteRepo) {
        try {
            return new LocalGit(repoName, path, remoteRepo);
        } catch (IOException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    private Repository initLocalRepo(File path) throws IOException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        File gitDit = new File(path.getAbsolutePath().concat(File.separator).concat(".git"));
        return gitDit.exists()
                ? repositoryBuilder
                .setGitDir(gitDit)
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build()
                : cloneRepo(path);
    }

    public boolean addRemote(String remoteUrl) {
        try {
            RemoteAddCommand remoteAddCommand = git.remoteAdd();
            remoteAddCommand.setName("origin");
            remoteAddCommand.setUri(new URIish(remoteUrl));
            return remoteAddCommand.call() != null;
        } catch (URISyntaxException | GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean init() {
        return init(path);
    }

    private boolean init(File path) {
        try {
            return Git.init().setDirectory(path).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public Repository cloneRepo() {
        try {
            return Git.cloneRepository()
                    .setURI(remoteGit.getCompleteUrl())
                    .setCredentialsProvider(remoteGit.credentials())
                    .setBare(false)
                    .setDirectory(path).call().getRepository();
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public Repository cloneRepo(File path) {
        try {
            return Git.cloneRepository()
                    .setURI(remoteGit.getCompleteUrl())
                    .setCredentialsProvider(remoteGit.credentials())
                    .setBare(false)
                    .setDirectory(path).call().getRepository();
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean add(String pattern) {
        try {
            return git.add().addFilepattern(pattern).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean commit(String message, String username, String email) {
        try {
            return git.commit().setMessage(message).setAuthor(username, email).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean tag(String tagName) {
        try {
            return git.tag().setName(tagName).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean push() {
        try {
            return git.push().setCredentialsProvider(remoteGit.credentials()).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public String currentBranch() {
        try {
            return this.git.getRepository().getBranch();
        } catch (IOException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean checkout(String branchName) {
        try {
            if (!this.currentBranch().equalsIgnoreCase(branchName)) {
                git.branchCreate()
                        .setName(branchName)
                        .call();
                return git.checkout()
                        .setName(branchName)
                        .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                        .setStartPoint("origin/" + branchName)
                        .call() != null;
            }
            return false;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean pullMaster() {
        PullCommand pull = git.pull();
        pull.setRemote("origin");
        pull.setRemoteBranchName("master");
        return true;
    }

    public boolean pull() {
        try {
            PullCommand pull = git.pull();
            pull.setRemote("origin");
            pull.setRemoteBranchName(git.getRepository().getBranch());
            return true;
        } catch (IOException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }

    public boolean push(String remote, String token) {
        try {
            return git.push().setRemote(remote).setCredentialsProvider(new UsernamePasswordCredentialsProvider(token, "")).call() != null;
        } catch (GitAPIException ex) {
            throw KnownException.SYSTEM_FAILURE.provide(ex);
        }
    }
}
