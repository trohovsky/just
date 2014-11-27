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

import org.junit.Test;

public class FieldDependenciesTest extends AbstractDependenciesTest {

	private static final String FIELD_DEPENDENCIES_CLASS = "com/github/trohovsky/just/test/app/FieldDependencies";

	@Test
	public void testAnnotation() {
		Set<String> externalClasses = new TreeSet<String>();
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithArray");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithClass");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/ClassForAnnotation");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/AnnotationWithEnum");
		externalClasses.add("com/github/trohovsky/just/test/lib/annotation/EnumForAnnotation");

		resolveUsedClassesAndVerify(FIELD_DEPENDENCIES_CLASS, externalClasses);
	}
}
