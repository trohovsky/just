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
package com.github.trohovsky.just.model;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

/**
 * Container class for collecting of dependencies.
 * 
 * @author Tomas Rohovsky
 */
public class Dependencies {

	private final Set<String> dependencies = new TreeSet<String>();

	public Set<String> get() {
		return Collections.unmodifiableSet(dependencies);
	}

	public void addName(final String name) {
		if (name == null) {
			return;
		}
		dependencies.add(name);
	}

	public void addInternalName(final String name) {
		if (name == null) {
			return;
		}
		addType(Type.getObjectType(name));
	}

	public void addInternalNames(final String[] names) {
		if (names == null) {
			return;
		}
		for (String name : names) {
			addInternalName(name);
		}
	}

	public void addDesc(final String desc) {
		addType(Type.getType(desc));
	}

	public void addMethodDesc(final String desc) {
		addType(Type.getReturnType(desc));
		final Type[] types = Type.getArgumentTypes(desc);
		for (Type type : types) {
			addType(type);
		}
	}

	public void addType(final Type t) {
		switch (t.getSort()) {
		case Type.ARRAY:
			addType(t.getElementType());
			break;
		case Type.OBJECT:
			addName(t.getInternalName());
			break;
		}
	}

	public void addSignature(final String signature, final SignatureVisitor signatureVisitor) {
		if (signature != null) {
			new SignatureReader(signature).accept(signatureVisitor);
		}
	}

	public void addTypeSignature(final String signature, final SignatureVisitor signatureVisitor) {
		if (signature != null) {
			new SignatureReader(signature).acceptType(signatureVisitor);
		}
	}

}
