package com.github.anilople.javajvm.instructions.math.shift;

import com.github.anilople.javajvm.instructions.BytecodeReader;
import com.github.anilople.javajvm.instructions.Instruction;
import com.github.anilople.javajvm.runtimedataarea.Frame;

/**
 * Operation
 * Logical shift right int
 *
 * Operand ..., result
 * Stack ..., value1, value2 →
 *
 * Description
 * Both value1 and value2 must be of type int . The values are popped
 * from the operand stack. An int result is calculated by shifting
 * value1 right by s bit positions, with zero extension, where s is the
 * value of the low 5 bits of value2. The result is pushed onto the
 * operand stack.
 *
 * Notes
 * If value1 is positive and s is value2 & 0x1f, the result is the same
 * as that of value1 >> s; if value1 is negative, the result is equal to
 * the value of the expression (value1 >> s) + (2 << ~s). The addition
 * of the (2 << ~s) term cancels out the propagated sign bit. The shift
 * distance actually used is always in the range 0 to 31, inclusive.
 *
 */
public class IUSHR implements Instruction {

    @Override
    public void fetchOperands(BytecodeReader bytecodeReader) {

    }

    @Override
    public void execute(Frame frame) {
        final int value2 = frame.getOperandStacks().popIntValue();
        final int value1 = frame.getOperandStacks().popIntValue();

        final int s = value2 & 0b0001_1111; // same as (value2 & 0x1f)
        final int result = value1 >>> s; // shifting value1 right by s bit position, with sign extension
        frame.getOperandStacks().pushIntValue(result);

        int nextPc = frame.getNextPc() + this.size();
        frame.setNextPc(nextPc);
    }

    @Override
    public int size() {
        return 1;
    }

}
