import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.ceylon.CeylonUtils;
import com.redhat.ceylon.cmr.impl.JULLogger;
import com.redhat.ceylon.compiler.Options;
import com.redhat.ceylon.compiler.js.JsCompiler;
import com.redhat.ceylon.compiler.loader.JsModuleManagerFactory;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.TypeCheckerBuilder;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Module;

/**
 * Some hack before a proper unit test harness is put in place
 *
 * @author Emmanuel Bernard <emmanuel@hibernate.org>
 */
public class MainForJsTest {
    
    public static void main(String[] args) throws Exception {
        Options opts = Options.parse(new ArrayList<String>(Arrays.asList(
                "-rep", "build/runtime",
                "-out", "build/test/node_modules", "-module")));
        final RepositoryManager repoman = CeylonUtils.makeRepositoryManager(opts.getRepos(),
                opts.getOutDir(), new JULLogger());
        System.out.println("Typechecking Ceylon test code...");
        TypeCheckerBuilder tcb = new TypeCheckerBuilder().verbose(false)
            .addSrcDirectory(new File("src/test/ceylon/check/check.ceylon"))
            //.addSrcDirectory(new File("../ceylon.language/test"))
            .moduleManagerFactory(new JsModuleManagerFactory())
            .usageWarnings(false);
        tcb.setRepositoryManager(repoman);
        TypeChecker typeChecker = tcb.getTypeChecker();
        typeChecker.process();
        if (typeChecker.getErrors() > 0) {
            System.exit(1);
        }
        System.out.println("Compiling without optimization");
        JsCompiler jsc = new JsCompiler(typeChecker, opts).stopOnErrors(false);
        if (jsc.generate()) {
            validateOutput(typeChecker, opts);
        } else {
            jsc.printErrors(System.out);
            System.out.println("Skipping output validation.");
        }
        System.out.println("Compiling with optimization");
        opts = Options.parse(new ArrayList<String>(Arrays.asList("-optimize", "-out",
                "build/test/nodeopt_modules", "-module")));
        jsc = new JsCompiler(typeChecker, opts).stopOnErrors(false);
        if (jsc.generate()) {
            validateOutput(typeChecker, opts);
        } else {
            jsc.printErrors(System.out);
            System.out.println("Skipping output validation.");
        }
    }

    static void validateOutput(TypeChecker typeChecker, Options opts)
            throws FileNotFoundException, IOException {
        int count=0;
        HashSet<Module> tested = new HashSet<Module>();
        for (PhasedUnit pu: typeChecker.getPhasedUnits().getPhasedUnits()) {
            Module mod = pu.getPackage().getModule();
            if (!tested.contains(mod)) {
                File generated = new File(String.format("%s/%s",
                        opts.getOutDir(), toOutputPath(mod)));
                File test = new File(String.format("src/test/ceylon/%s", toTestPath(mod, opts.isOptimize())));
                if (test.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(test));
                    BufferedReader outputReader = new BufferedReader(new FileReader(generated));
                    try {
                        int i=0;
                        while (reader.ready() && outputReader.ready()) {
                            i++;
                            String actual = outputReader.readLine();
                            String expected = reader.readLine();
                            if (!expected.equals(actual) && !expected.trim().startsWith("//")) {
                                System.err.printf("error at %s: %d%n", test.getPath(), i); 
                                System.err.println("expected: " + expected);
                                System.err.println("  actual: " + actual);
                                break;
                            }
                        }
                        count++;
                    } finally {
                        reader.close();
                        outputReader.close();
                    }
                }
                tested.add(mod);
            }
        }
        System.out.printf("Ran %d tests%n", count);
    }

    private static String toOutputPath(Module mod) {
        String modname = mod.isDefault() ? "default" :
            mod.getNameAsString().replace('.', '/');
        return mod.isDefault() ? "default/default.js" :
            String.format("%1$s/%2$s/%1$s-%2$s.js", modname, mod.getVersion());
    }

    private static String toTestPath(Module mod, boolean opt) {
        String modname = mod.isDefault() ? "default" :
            mod.getNameAsString().replace('.', '/');
        return String.format("%1$s/%1$s%2$s.js", modname, opt? ".jsopt" : "");
    }
}
