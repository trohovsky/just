/*
 * Copyright 2014 Tomas Rohovsky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.trohovsky.just.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureVisitor;

import com.github.trohovsky.just.model.Dependencies;

/**
 * Visitor for collecting of method dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class MethodDependenciesVisitor extends MethodVisitor {

	private final Dependencies dependencies;
	private final AnnotationVisitor annotationVisitor;
	private final SignatureVisitor signatureVisitor;

	public MethodDependenciesVisitor(final Dependencies dependencies, final AnnotationVisitor annotationVisitor,
			SignatureVisitor signatureVisitor) {
		super(Opcodes.ASM5);
		this.dependencies = dependencies;
		this.annotationVisitor = annotationVisitor;
		this.signatureVisitor = signatureVisitor;
	}

	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return annotationVisitor;
	}

	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

	// Java 8 - JSR 308: Annotations on Java Types
	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

	@Override
	public void visitTypeInsn(int opcode, String type) {
		dependencies.addType(Type.getObjectType(type));
	}

	@Override
	public void visitFieldInsn(int opcode, String owner, String name, String desc) {
		dependencies.addInternalName(owner);
		dependencies.addDesc(desc);
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
		dependencies.addInternalName(owner);
		dependencies.addMethodDesc(desc);
	}

	// TODO test
	@Override
	public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
		dependencies.addDesc(desc);
	}

	// TODO test
	@Override
	public void visitLdcInsn(Object cst) {
		if (cst instanceof Type) {
			dependencies.addType((Type) cst);
		}
	}

	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		dependencies.addDesc(desc);
	}

	// Java 8 - JSR 308: Annotations on Java Types
	@Override
	public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
		dependencies.addInternalName(type);
	}

	// Java 8 - JSR 308: Annotations on Java Types
	@Override
	public AnnotationVisitor visitTryCatchAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

	@Override
	public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
		if (signature == null) {
			dependencies.addDesc(desc);
		} else {
			dependencies.addTypeSignature(signature, signatureVisitor);
		}
	}

	@Override
	public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
			int[] index, String desc, boolean visible) {
		dependencies.addDesc(desc);
		return annotationVisitor;
	}

}
