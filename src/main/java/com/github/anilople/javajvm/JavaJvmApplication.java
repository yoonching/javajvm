package com.github.anilople.javajvm;

import com.github.anilople.javajvm.classpath.Classpath;
import com.github.anilople.javajvm.command.Command;
import com.github.anilople.javajvm.heap.JvmClass;
import com.github.anilople.javajvm.heap.JvmClassLoader;
import com.github.anilople.javajvm.heap.JvmMethod;
import com.github.anilople.javajvm.instructions.Instruction;
import com.github.anilople.javajvm.runtimedataarea.Frame;
import com.github.anilople.javajvm.runtimedataarea.JvmThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class JavaJvmApplication {

    private static final Logger logger = LoggerFactory.getLogger(JavaJvmApplication.class);

    private Command command;

    private Classpath classpath;

    /**
     * initial a jvm
     *
     * @param command
     */
    public JavaJvmApplication(Command command) {
        this.command = command;
        if(!Classpath.isInitialized()) {
            // just use jre now
            // may add classpath todo
            Classpath.initialize(command.getOptions().getXjre());
        }
        this.classpath = Classpath.getInstance();
    }

    /**
     * in java, there 4 way to use java command
     * 1. java [-options] class [args...]
     * 2. java [-options] -jar jarfile [args...]
     * 3. javaw [-options] class [args...]
     * 4. javaw [-options] -jar jarfile [args...]
     * <p>
     * we are using 1 or 2, not using 3 and 4
     *
     * @param args [args...] above
     */
    public static void main(String[] args) {
        if(args.length <= 0) {
            printUsage();
        } else {
            Command command = Command.parse(args);
            logger.debug("command : {}", command);
            if (command.getOptions().isVersionFlag()) {
                System.out.println("java version " + "0.0.1");
                System.out.println("Interpreted Mode");
            } else if (command.getOptions().isHelpFlag()) {
                printUsage();
            } else if (null != command.getClassName()) {
                JavaJvmApplication javaJvmApplication = new JavaJvmApplication(command);
                javaJvmApplication.start();
            }
        }
    }

    /**
     * print usage to user
     */
    public static void printUsage() {
        // copy them from official
        final InputStream inputStream = JavaJvmApplication.class.getClassLoader().getResourceAsStream("usage.txt");
        final byte[] buffer = new byte[1024 * 1024];
        final int length;
        try {
            length = inputStream.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException("cannot read usage.txt", e);
        }
        String usageContent = new String(buffer, 0, length);
        System.out.println(usageContent);
    }

    public static void interpret(JvmMethod jvmMethod) {
        logger.debug("interpret method {}", jvmMethod);
        JvmThread jvmThread = new JvmThread();
        jvmThread.pushFrame(new Frame(jvmThread, jvmMethod));
        loop(jvmThread);
    }

    public static void loop(JvmThread jvmThread) {
        logger.trace("start loop: {}", jvmThread);

        while (jvmThread.existFrame()) {
            jvmThread.currentFrame().traceStatus();
            // get next instruction from frame (there is a pc register in frame)
            Instruction instruction = jvmThread.currentFrame().readNextInstruction();
            // change nextPc in top frame
            instruction.execute(jvmThread.currentFrame());
        }

        logger.trace("loop finished: {}", jvmThread);
    }

    /**
     * run jvm
     */
    public void start() {
        String className = command.getClassName().replace('.', '/');
        logger.debug("class name = {}", className);

        JvmClassLoader jvmClassLoader = JvmClassLoader.getInstance();
        JvmClass jvmClass = jvmClassLoader.loadClass(className);

        if(!jvmClass.existMethod("main", "([Ljava/lang/String;)V")) {
            throw new RuntimeException("cannot find main method in class " + jvmClass.getName());
        }

        for(JvmMethod jvmMethod : jvmClass.getJvmMethods()) {
            logger.debug("jvm method : {}", jvmMethod);
            if("main".equals(jvmMethod.getName())) {
                interpret(jvmMethod);
            }
        }
    }
}
