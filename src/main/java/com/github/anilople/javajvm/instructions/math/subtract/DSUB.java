package com.github.anilople.javajvm.instructions.math.subtract;

import com.github.anilople.javajvm.instructions.BytecodeReader;
import com.github.anilople.javajvm.instructions.Instruction;
import com.github.anilople.javajvm.runtimedataarea.Frame;

/**
 * Operation
 * Subtract double
 *
 * Operand ..., value1, value2 →
 * Stack ..., result
 *
 * Description
 * Both value1 and value2 must be of type double . The values are
 * popped from the operand stack and undergo value set conversion
 * (§2.8.3), resulting in value1' and value2'. The double result is
 * value1' - value2'. The result is pushed onto the operand stack.
 * For double subtraction, it is always the case that a-b produces
 * the same result as a+(-b) . However, for the dsub instruction,
 * subtraction from zero is not the same as negation, because if x is
 * +0.0 , then 0.0-x equals +0.0 , but -x equals -0.0 .
 * The Java Virtual Machine requires support of gradual underflow as
 * defined by IEEE 754. Despite the fact that overflow, underflow, or
 * loss of precision may occur, execution of a dsub instruction never
 * throws a run-time exception.
 */
public class DSUB implements Instruction {

    @Override
    public void fetchOperands(BytecodeReader bytecodeReader) {

    }

    @Override
    public void execute(Frame frame) {
        double value2 = frame.getOperandStacks().popDoubleValue();
        double value1 = frame.getOperandStacks().popDoubleValue();

        double result = value1 - value2;

        frame.getOperandStacks().pushDoubleValue(result);

        int nextPc = frame.getNextPc() + this.size();
        frame.setNextPc(nextPc);
    }

    @Override
    public int size() {
        return 1;
    }

}
