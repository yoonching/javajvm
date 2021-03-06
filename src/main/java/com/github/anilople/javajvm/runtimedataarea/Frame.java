package com.github.anilople.javajvm.runtimedataarea;

import com.github.anilople.javajvm.heap.JvmMethod;
import com.github.anilople.javajvm.instructions.BytecodeReader;
import com.github.anilople.javajvm.instructions.Instruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * A frame is used to store data and partial results, as well as to perform dynamic
 * linking, return values for methods, and dispatch exceptions.
 */
public class Frame {

    private static final Logger logger = LoggerFactory.getLogger(Frame.class);

    private JvmThread jvmThread;

    private LocalVariables localVariables;

    private OperandStacks operandStacks;

    private JvmMethod jvmMethod;

    /**
     * every frame keep own pc register,
     * memory thread's pc
     */
    private int nextPc;

    private Frame() {
    }

    public Frame(JvmThread jvmThread, JvmMethod jvmMethod) {
        this.jvmThread = jvmThread;
        this.localVariables = new LocalVariables(jvmMethod.getMaxLocals());
        this.operandStacks = new OperandStacks(jvmMethod.getMaxStack());
        this.jvmMethod = jvmMethod;
    }

    /**
     * max locals in jvm method must >= max locals in local variables
     * @param jvmThread
     * @param jvmMethod
     * @param localVariables
     * @throws RuntimeException
     */
    public Frame(JvmThread jvmThread, JvmMethod jvmMethod, LocalVariables localVariables) {
        if(jvmMethod.getMaxLocals() < localVariables.getMaxLocals()) {
            String message = String.format(
                "max locals in jvm method must >= max locals in local variables!! method = %d, local variables = %d",
                jvmMethod.getMaxLocals(),
                localVariables.getMaxLocals()
            );
            throw new RuntimeException(message);
        }

        // maybe we need to grow local variables
        if(localVariables.getMaxLocals() < jvmMethod.getMaxLocals()) {
            localVariables.growMaxLocals(jvmMethod.getMaxLocals());
        }

        this.jvmThread = jvmThread;
        this.localVariables = localVariables;
        this.operandStacks = new OperandStacks(jvmMethod.getMaxStack());
        this.jvmMethod = jvmMethod;
    }

    /**
     * logger level is trace,
     * frame status
     */
    public void traceStatus() {
        logger.trace("jvmThread: {}, jvmMethod: {}", jvmThread, jvmMethod);
        logger.trace("localVariables: {}", localVariables);
        logger.trace("operandStacks: {}", operandStacks);
    }

    /**
     * read next instruction started by pc register
     * @return
     */
    public Instruction readNextInstruction() {
        // method -> method's code
        byte[] bytecode = this.getJvmMethod().getCode();

        int pc = this.getNextPc();
        BytecodeReader bytecodeReader = new BytecodeReader(Arrays.copyOfRange(bytecode, pc, bytecode.length));

        Instruction instruction = Instruction.readInstruction(bytecodeReader);
        logger.debug("read instruction: {}", instruction);

        // fetch operands (may fetch nothing)
        instruction.fetchOperands(bytecodeReader);

        return instruction;
    }

    public JvmThread getJvmThread() {
        return jvmThread;
    }

    public LocalVariables getLocalVariables() {
        return localVariables;
    }

    public OperandStacks getOperandStacks() {
        return operandStacks;
    }

    public JvmMethod getJvmMethod() {
        return jvmMethod;
    }

    public int getNextPc() {
        return nextPc;
    }

    public void setNextPc(int nextPc) {
        this.nextPc = nextPc;
    }
}
