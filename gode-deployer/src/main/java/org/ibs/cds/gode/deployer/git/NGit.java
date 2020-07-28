package org.ibs.cds.gode.deployer.git;

import org.ibs.cds.gode.system.NativeCmd;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public class NGit {

    private final Path directory;

    public NGit(Path directory){
        this.directory = directory;
    }

    private String[] action(String[] arguments, String... actions){
        return Stream.of(actions,arguments).flatMap(Stream::of).toArray(String[]::new);
    }

    public int init(String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "init"));
    }

    public int stage(String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "add", "-A"));
    }

    public int commit(String message, String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "commit", "-m", message));
    }

    public int add(String pattern,String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "add", pattern));
    }

    public int checkout(String branch,String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args, "git", "checkout", branch));
    }

    public int checkout$b(String branch,String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "checkout", "-b", branch));
    }

    public int push(String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory, action(args,"git", "push"));
    }

    public int clone(String originUrl,String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory.getParent(), action(args,"git", "clone", originUrl, directory.getFileName().toString()));
    }

    public int cmd(String command, String... args) throws IOException, InterruptedException {
        return NativeCmd.run(directory.getParent(), action(args,"git", command));
    }

}
