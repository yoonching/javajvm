package com.github.anilople.javajvm.classfile.constantinfo;

import com.github.anilople.javajvm.classfile.ClassFile;
import com.github.anilople.javajvm.constants.ConstantPoolTags;

public class ConstantFieldrefInfo extends ConstantPoolInfo {

    public static final byte TAG = ConstantPoolTags.CONSTANT_Fieldref;

    private short classIndex;

    private short nameAndTypeIndex;

    @Override
    public byte getTag() {
        return TAG;
    }

    public ConstantFieldrefInfo(ClassFile classFile, ClassFile.ClassReader classReader) {
        super(classFile);
        this.classIndex = classReader.readU2();
        this.nameAndTypeIndex = classReader.readU2();
    }

    public short getClassIndex() {
        return classIndex;
    }

    public short getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }
}
