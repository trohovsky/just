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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureVisitor;

import com.github.trohovsky.just.model.Dependencies;

/**
 * Visitor for collecting of signature dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class SignatureDependenciesVisitor extends SignatureVisitor {

	private final Dependencies dependencies;
	private String signatureClassName;

	public SignatureDependenciesVisitor(final Dependencies dependencies) {
		super(Opcodes.ASM5);
		this.dependencies = dependencies;
	}

	@Override
	public void visitClassType(final String name) {
		signatureClassName = name;
		dependencies.addInternalName(name);
	}

	@Override
	public void visitInnerClassType(final String name) {
		signatureClassName = signatureClassName + "$" + name;
		dependencies.addInternalName(signatureClassName);
	}
}
