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

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

public class ReaderTest {

	private static final String APP_PATH = "target/test-app.jar";
	private static final String LIB_PATH = "target/test-lib.jar";
	private static final String NULL_PATH = null;
	private static final String[] NULL_PATHS = { null, null };

	// listClasses

	@Test(expected = IllegalArgumentException.class)
	public void testListClassesNullPath() throws IOException {
		Reader.from(NULL_PATH).listClasses();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testListClassesNullPaths() throws IOException {
		Reader.from(NULL_PATHS).listClasses();
	}

	@Test
	public void testListClassesIncludes() throws IOException {
		Set<String> externalClasses = Reader.from(LIB_PATH).includes("com/github/trohovsky/just/test/lib/includes")
				.listClasses();

		Set<String> expectedClasses = new TreeSet<String>();
		expectedClasses.add("com/github/trohovsky/just/test/lib/includes/IncludedClass");
		expectedClasses.add("com/github/trohovsky/just/test/lib/includes/excludes/ExcludedClass");
		Assert.assertArrayEquals(expectedClasses.toArray(), externalClasses.toArray());
	}

	@Test
	public void testListTypesIncludesExcludes() throws IOException {
		Set<String> externalClasses = Reader.from(LIB_PATH).includes("com/github/trohovsky/just/test/lib/includes")
				.excludes("com/github/trohovsky/just/test/lib/includes/excludes/ExcludedClass").listClasses();

		Set<String> expectedClasses = new TreeSet<String>();
		expectedClasses.add("com/github/trohovsky/just/test/lib/includes/IncludedClass");
		Assert.assertArrayEquals(expectedClasses.toArray(), externalClasses.toArray());
	}

	// readClassesWithDependencies

	@Test(expected = IllegalArgumentException.class)
	public void testReadClassesWithDependenciesNullPath() throws IOException {
		Reader.from(NULL_PATH).readClassesWithDependencies();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testReadClassesWithDependenciesListTypesNullPaths() throws IOException {
		Reader.from(NULL_PATHS).readClassesWithDependencies();
	}

	@Test
	public void testReadClassesWithDependenciesIncludes() throws IOException {
		Map<String, Set<String>> classesWithDependencies = Reader.from(APP_PATH)
				.includes("com/github/trohovsky/just/test/app/includes").readClassesWithDependencies();

		Set<String> expectedClasses = new TreeSet<String>();
		expectedClasses.add("com/github/trohovsky/just/test/app/includes/IncludedClass");
		expectedClasses.add("com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass");
		Assert.assertArrayEquals(expectedClasses.toArray(), classesWithDependencies.keySet().toArray());
	}

	@Test
	public void testReadClassesWithDependenciesIncludesExcludes() throws IOException {
		Map<String, Set<String>> classesWithDependencies = Reader.from(APP_PATH)
				.includes("com/github/trohovsky/just/test/app/includes/")
				.excludes("com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass")
				.readClassesWithDependencies();

		Set<String> expectedClasses = new TreeSet<String>();
		expectedClasses.add("com/github/trohovsky/just/test/app/includes/IncludedClass");
		Assert.assertArrayEquals(expectedClasses.toArray(), classesWithDependencies.keySet().toArray());
	}

}
