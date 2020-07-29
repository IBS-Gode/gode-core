package org.ibs.cds.gode.deployer.git;

import org.ibs.cds.gode.entity.generic.Try;
import org.ibs.cds.gode.exception.KnownException;
import org.ibs.cds.gode.system.NativeCmd;

import java.io.File;
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

    public boolean isRepo(){
        return new File(this.directory.toFile().getAbsolutePath().concat(File.separator).concat(".git")).exists();
    }

    private int run(String... commands){
        return run(directory, commands);
    }

    private int run(Path path, String... commands){
        return Try
                .code( (String[] k)-> NativeCmd.run(path, k))
                .catchWith(KnownException.SYSTEM_FAILURE)
                .run(commands).orElse(-1);
    }

    public int init(String... args){
        return run(action(args, "git","init"));
    }

    public int stage(String... args) {
        return run(action(args, "git","add", "-A"));
    }

    public int commit(String message, String... args) {
        return run(action( args,"git","commit", "-m",message));
    }

    public int add(String pattern,String... args) {
        return run(action(args,"git", "add", pattern));
    }

    public int checkout(String branch,String... args) {
        return run(action(args, "git", "checkout", branch));
    }

    public int checkout$b(String branch,String... args) {
        return run(action(args,"git", "checkout", "-b", branch));
    }

    public int push(String... args) {
        return run(action(args,"git", "push"));
    }

    public int clean(String... args) {
        return run(action(args,"git", "clean"));
    }

    public int reset(String... args) {
        return run(action(args,"git", "reset"));
    }

    public int pull(String... args) {
        return run(action(args,"git", "pull"));
    }

    public int status(String... args){
        return run(action(args,"git", "status"));
    }

    public int remote$add$origin(String url){
        return run( "git", "remote", "add", "origin",url);
    }

    public int remote(String... args){
        return run(action(args , "git", "remote"));
    }

    public int clone(String originUrl,String... args){
        return run(directory.getParent(), action(args,"git", "clone", originUrl, directory.getFileName().toString()));
    }

    public int cmd(String... args) {
        return run(action(args,"git"));
    }

}
