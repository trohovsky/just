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

import java.util.Set;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.signature.SignatureVisitor;

import com.github.trohovsky.just.model.Dependencies;

/**
 * Visitor for collecting of class dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class ClassDependenciesVisitor extends ClassVisitor {

	private final Dependencies dependencies;
	private final AnnotationVisitor annotationVisitor;
	private final SignatureVisitor signatureVisitor;
	private final FieldVisitor fieldVisitor;
	private final MethodVisitor methodVisitor;

	public ClassDependenciesVisitor(final Dependencies dependencies, final AnnotationVisitor annotationVisitor,
			final SignatureVisitor signatureVisitor, final FieldVisitor fieldVisitor, final MethodVisitor methodVisitor) {
		super(Opcodes.ASM5);
		this.dependencies = dependencies;
		this.annotationVisitor = annotationVisitor;
		this.signatureVisitor = signatureVisitor;
		this.fieldVisitor = fieldVisitor;
		this.methodVisitor = methodVisitor;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		if (signature == null) {
			dependencies.addInternalName(superName);
			dependencies.addInternalNames(interfaces);
		} else {
			dependencies.addSignature(signature, signatureVisitor);
		}
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
	public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
		if (signature == null) {
			dependencies.addDesc(desc);
		} else {
			dependencies.addTypeSignature(signature, signatureVisitor);
		}
		if (value instanceof Type) {
			dependencies.addType((Type) value);
		}
		return fieldVisitor;
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (signature == null) {
			dependencies.addMethodDesc(desc);
		} else {
			dependencies.addSignature(signature, signatureVisitor);
		}
		dependencies.addInternalNames(exceptions);
		return methodVisitor;
	}

	public Set<String> getClasses() {
		return dependencies.get();
	}

}
