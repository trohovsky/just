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
package com.github.trohovsky.just;

import java.util.Set;
import java.util.TreeSet;
import org.junit.Ignore;
import org.junit.Test;

public class MethodDependenciesTest extends AbstractDependenciesTest {

	private static final String METHOD_DEPENDENCIES_CLASS = "com/github/trohovsky/just/test/app/MethodDependencies";
	private static final String INTERFACE_METHOD_DEPENDENCIES_CLASS = "com/github/trohovsky/just/test/app/InterfaceMethodDependencies";

	@Test
	public void testMethodAnnotation() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithArray");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithClass");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/ClassForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithEnum");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/EnumForAnnotation");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testAnnotationDefault() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/AnnotationDefault");

		resolveUsedClassesAndVerify(INTERFACE_METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testFieldInstruction() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/FieldInstruction");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	// TODO how to test, so that Constant will be added by visitLdcInsn
	@Test
	public void testLoadConstant() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/Constant");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testLocalVariable() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/LocalVariable");
		externalClasses.add("com/github/trohovsky/just/test/lib/method/LocalVariableArray");

		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/LocalVariableTypeParameter");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	/**
	 * Annotations on local variables are currently discarded by the compiler.
	 * See http://osdir.com/ml/java.objectweb.asm/2008-01/msg00007.html
	 */
	@Ignore
	@Test
	public void testLocalVariableAnnotation() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/LocalVariableAnnotation");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testMultiANewArray() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/MultiANewArray");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);

	}

	@Test
	public void testParameterAnnotation() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/ParameterAnnotation");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testTryCatchBlock() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/method/TryCatchException");

		resolveUsedClassesAndVerify(METHOD_DEPENDENCIES_CLASS, externalClasses);
	}
}
