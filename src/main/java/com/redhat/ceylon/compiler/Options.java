package com.redhat.ceylon.compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Represents all the options for compiling.
 * 
 * @author Enrique Zamudio
 */
public class Options {

    private List<String> repos = new ArrayList<String>();
    private String user;
    private String pass;
    private String srcDir = "source";
    private String outDir = "modules";
    private boolean optimize;
    private boolean modulify = true;
    private boolean indent = true;
    private boolean comment = true;
    private boolean verbose;
    private boolean profile;
    private boolean help;
    private boolean version;
    private boolean stdin;

    /** Creates and returns an Options object from a command-list argument list. The list itself
     * is modified, so at the end it only contains the files to compile. */
    public static Options parse(List<String> args) {
        Options opts = new Options();
        //Review all non-arg options
        opts.version = findOption("-version", args, true);
        opts.help = findOption("-help", args, true);
        opts.optimize = findOption("-optimize", args, true);
        opts.modulify = !findOption("-nomodule", args, true);
        if (args.contains("-noindent") || args.contains("-compact")) {
            opts.indent=false;
            args.remove("-noindent");
        }
        if (args.contains("-nocomments") || args.contains("-compact")) {
            opts.comment=false;
            args.remove("-nocomments");
            args.remove("-compact");
        }
        opts.verbose = findOption("-verbose", args, true);
        opts.profile = findOption("-profile", args, true);
        opts.stdin = findOption("--", args, true);
        //Review arg options
        for (Iterator<String> iter = args.iterator(); iter.hasNext();) {
            String s = iter.next();
            if (s.startsWith("-")) {
                iter.remove();
                //Get the option's value
                if (iter.hasNext()) {
                    String v = iter.next();
                    iter.remove();
                    if ("-rep".equals(s)) {
                        opts.addRepo(v);
                    } else if ("-user".equals(s)) {
                        opts.user=v;
                    } else if ("-pass".equals(s)) {
                        opts.pass=v;
                    } else if ("-src".equals(s)) {
                        opts.srcDir=v;
                    } else if ("-out".equals(s)) {
                        opts.outDir=v;
                    } else {
                        System.err.printf("Unrecognized option %s %s%n", s, v);
                    }
                }
            }
        }
        return opts;
    }

    /** Find all the repos specified in the argument list (pairs of "-rep x").
     * @param args The argument list from which to parse repositories
     * @param remove If true, removes found repos from arguments, otherwise leaves list intact.
     * @return The list of found repositories. */
    public static List<String> findRepos(List<String> args, boolean remove) {
        ArrayList<String> repos = new ArrayList<String>(args.size() / 2);
        for (Iterator<String> iter = args.iterator(); iter.hasNext();) {
            String s = iter.next();
            if ("-rep".equals(s)) {
                if (remove) {
                    iter.remove();
                }
                if (iter.hasNext()) {
                    s = iter.next();
                    repos.add(s);
                    if (remove) {
                        iter.remove();
                    }
                }
            }
        }
        return repos;
    }

    /** Finds the value for an option that requires value. Can remove it from the original list if needed.
     * @param optionName The name for the option (usually starts with "-")
     * @param args The list of arguments where to look for the option and its value
     * @param remove If true, removes the option and its value from the list.
     * @return The value for the specified option, or null if not found. */
    public static String findOptionValue(String optionName, List<String> args, boolean remove) {
        int idx = args.indexOf(optionName);
        if (idx >=0 && idx < args.size() - 2 && !args.get(idx+1).startsWith("-")) {
            if (remove) {
                args.remove(idx);
                return args.remove(idx);
            } else {
                return args.get(idx+1);
            }
        }
        return null;
    }

    /** Finds the specified option among the arguments, and removes it if needed.
     * @param name The option name (usually starts with "-")
     * @param args The list of arguments where to look for the option
     * @param remove If true, removes the option from the arguments.
     * @return true if the option was found, false otherwise. */
    public static boolean findOption(String name, List<String> args, boolean remove) {
        int idx = args.indexOf(name);
        if (idx >= 0) {
            if (remove) {
                args.remove(idx);
            }
            return true;
        }
        return false;
    }

    /** Returns the list of repositories that were parsed from the command line. */
    public List<String> getRepos() {
        return repos;
    }
    public void addRepo(String repo) {
        repos.add(repo);
    }

    public String getUser() {
        return user;
    }
    public String getPass() {
        return pass;
    }
    public String getSrcDir() {
        return srcDir;
    }
    public String getOutDir() {
        return outDir;
    }
    public boolean isOptimize() {
        return optimize;
    }
    public boolean isModulify() {
        return modulify;
    }
    public boolean isIndent() {
        return indent;
    }
    public boolean isComment() {
        return comment;
    }
    public boolean isVerbose() {
        return verbose;
    }
    public boolean isProfile() {
        return profile;
    }
    public boolean isVersion() {
        return version;
    }
    public boolean isStdin() {
        return stdin;
    }
    public boolean isHelp() {
        return help;
    }

}