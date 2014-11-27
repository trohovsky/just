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

import org.junit.Assert;
import org.junit.Test;

import com.github.trohovsky.just.DependencyUtils;

public class ClassDependenciesTest extends AbstractDependenciesTest {

	private static final String CLASS_DEPENDENCIES_CLASS = "com/github/trohovsky/just/test/app/ClassDependencies";

	@Test
	public void testInheritance() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/SuperClass");
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/Interface");

		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/ClassTypeParameter");
		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/SuperClassTypeParameter");
		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/InterfaceTypeParameter");

		resolveUsedClassesAndVerify(CLASS_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testAnnotations() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithArray");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithClass");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/ClassForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithEnum");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/EnumForAnnotation");

		resolveUsedClassesAndVerify(CLASS_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testFields() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/Field");
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/FieldArray");

		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/FieldTypeParameter");

		resolveUsedClassesAndVerify(CLASS_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testMethodSignature() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/MethodReturnType");
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/MethodArgument");
		externalClasses.add("com/github/trohovsky/just/test/lib/classtype/MethodException");

		externalClasses.add("com/github/trohovsky/just/test/lib/typeparameter/MethodTypeParameter");

		resolveUsedClassesAndVerify(CLASS_DEPENDENCIES_CLASS, externalClasses);
	}

	@Test
	public void testUnusedClasses() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/gitbub/trohovsky/just/test/lib/other/Unused");

		Set<String> unusedTypes = DependencyUtils.subtract(externalClasses, classesWithDependencies);
		Assert.assertArrayEquals(externalClasses.toArray(), unusedTypes.toArray());
	}

}
