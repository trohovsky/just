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
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.github.trohovsky.just.model.Dependencies;

/**
 * Visitor for collecting of annotation dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class AnnotationDependenciesVisitor extends AnnotationVisitor {

	private final Dependencies dependencies;

	public AnnotationDependenciesVisitor(final Dependencies dependencies) {
		super(Opcodes.ASM5);
		this.dependencies = dependencies;
	}

	@Override
	public void visit(String name, Object value) {
		if (value instanceof Type) {
			dependencies.addType((Type) value); // @Annotation(String.class)
		}
	}

	@Override
	public void visitEnum(String name, String desc, String value) {
		dependencies.addDesc(desc);
	}

	@Override
	public AnnotationVisitor visitAnnotation(String name, String desc) {
		dependencies.addDesc(desc);
		return this;
	}

	@Override
	public AnnotationVisitor visitArray(String name) {
		return this;
	}

}
