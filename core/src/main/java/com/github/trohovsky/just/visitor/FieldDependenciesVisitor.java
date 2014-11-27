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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import com.github.trohovsky.just.model.Dependencies;

/**
 * Visitor for collecting of field dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class FieldDependenciesVisitor extends FieldVisitor {

	private final Dependencies dependencies;
	private final AnnotationVisitor annotationVisitor;

	public FieldDependenciesVisitor(final Dependencies dependencies, final AnnotationVisitor annotationVisitor) {
		super(Opcodes.ASM5);
		this.dependencies = dependencies;
		this.annotationVisitor = annotationVisitor;
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

}
