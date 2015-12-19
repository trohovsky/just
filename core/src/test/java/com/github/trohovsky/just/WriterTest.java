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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WriterTest {

	private static final String NULL_PATH = null;
	private static final String[] NULL_PATHS = { null, null };
	private static final String SRC_DIR = "target/";
	private static final String DST_DIR = "target/output/";
	private static final String SRC_APP_DIR = SRC_DIR + "test-app";
	private static final String SRC_LIB_DIR = SRC_DIR + "test-lib";
	private static final String SRC_APP_JAR = SRC_DIR + "test-app.jar";
	private static final String SRC_LIB_JAR = SRC_DIR + "test-lib.jar";
	private static final String DST_APP_DIR = DST_DIR + "test-app/";
	private static final String DST_LIB_DIR = DST_DIR + "test-lib/";
	private static final String DST_APP_JAR = DST_DIR + "test-app.jar";
	private static final String DST_LIB_JAR = DST_DIR + "test-lib.jar";

	@Before
	public void setUp() {
		FileUtils.delete(DST_DIR);
	}

	// null values

	@Test(expected = IllegalArgumentException.class)
	public void testCopyFromNullPath() throws IOException {
		Writer.from(NULL_PATH).copyTo(DST_DIR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCopyFromNullPaths() throws IOException {
		Writer.from(NULL_PATHS).copyTo(DST_DIR);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCopyToNullPath() throws IOException {
		Writer.from(SRC_APP_JAR).copyTo(NULL_PATHS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCopyToNullPathsPath() throws IOException {
		Writer.from(SRC_APP_JAR).copyTo(NULL_PATHS);
	}

	// directories

	@Test
	public void testCopyDirToDir() throws Exception {
		Writer.from(SRC_APP_DIR).copyTo(DST_APP_DIR);

		Set<String> expectedClasses = Reader.from(SRC_APP_DIR).listClasses();
		Set<String> actualClasses = Reader.from(DST_APP_DIR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	@Test
	public void testCopyNDirsToNDirs() throws Exception {
		Writer.from(SRC_APP_DIR, SRC_LIB_DIR).copyTo(DST_APP_DIR, DST_LIB_DIR);

		Set<String> expectedClassesApp = Reader.from(SRC_APP_DIR).listClasses();
		Set<String> actualClassesApp = Reader.from(DST_APP_DIR).listClasses();
		Assert.assertArrayEquals(expectedClassesApp.toArray(), actualClassesApp.toArray());

		Set<String> expectedClassesLib = Reader.from(SRC_LIB_DIR).listClasses();
		Set<String> actualClassesLib = Reader.from(DST_LIB_DIR).listClasses();
		Assert.assertArrayEquals(expectedClassesLib.toArray(), actualClassesLib.toArray());
	}

	@Test
	public void testCopyNDirsTo1Dir() throws Exception {
		Writer.from(SRC_APP_DIR, SRC_LIB_DIR).copyTo(DST_APP_DIR);

		Set<String> expectedClasses = new TreeSet<String>(Reader.from(SRC_APP_DIR).listClasses());
		expectedClasses.addAll(Reader.from(SRC_LIB_DIR).listClasses());
		Set<String> actualClasses = Reader.from(DST_APP_DIR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	@Test
	public void testCopyAndReduceDirToDir() throws Exception {
		String excludedClass = "com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass";
		Writer.from(SRC_APP_DIR).excludes(excludedClass).copyTo(DST_APP_DIR);

		Set<String> expectedClasses = Reader.from(SRC_APP_DIR).excludes(excludedClass).listClasses();
		Set<String> actualClasses = Reader.from(DST_APP_DIR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	// JARs

	@Test
	public void testCopyJarToDir() throws Exception {
		Writer.from(SRC_APP_JAR).copyTo(DST_DIR);

		Set<String> expectedClasses = Reader.from(SRC_APP_JAR).listClasses();
		Set<String> actualClasses = Reader.from(DST_APP_JAR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	@Test
	public void testCopyAndReduceJarToDir() throws Exception {
		String excludedClass = "com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass";
		Writer.from(SRC_APP_JAR).excludes(excludedClass).copyTo(DST_DIR);

		Set<String> expectedClasses = Reader.from(SRC_APP_JAR).excludes(excludedClass).listClasses();
		Set<String> actualClasses = Reader.from(DST_APP_JAR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	@Test
	public void testCopyAndReduceNJarsTo1Dir() throws Exception {
		String[] excludedClasses = { "com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass",
				"com/github/trohovsky/just/test/lib/includes/excludes/ExcludedClass" };
		Writer.from(SRC_APP_JAR, SRC_LIB_JAR).excludes(excludedClasses).copyTo(DST_DIR);

		Set<String> expectedClassesApp = Reader.from(SRC_APP_JAR).excludes(excludedClasses).listClasses();
		Set<String> actualClassesApp = Reader.from(DST_APP_JAR).listClasses();
		Assert.assertArrayEquals(expectedClassesApp.toArray(), actualClassesApp.toArray());

		Set<String> expectedClassesLib = Reader.from(SRC_LIB_JAR).excludes(excludedClasses).listClasses();
		Set<String> actualClassesLib = Reader.from(DST_LIB_JAR).listClasses();
		Assert.assertArrayEquals(expectedClassesLib.toArray(), actualClassesLib.toArray());
	}

	@Test
	public void testCopyAndReduceJarToJar() throws Exception {
		String excludedClass = "com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass";
		Writer.from(SRC_APP_JAR).excludes(excludedClass).copyTo(DST_APP_JAR);

		Set<String> expectedClasses = Reader.from(SRC_APP_JAR).excludes(excludedClass).listClasses();
		Set<String> actualClasses = Reader.from(DST_APP_JAR).listClasses();
		Assert.assertArrayEquals(expectedClasses.toArray(), actualClasses.toArray());
	}

	@Test
	public void testCopyAndReduceNJarsToNJars() throws Exception {
		String[] excludedClasses = { "com/github/trohovsky/just/test/app/includes/excludes/ExcludedClass",
				"com/github/trohovsky/just/test/lib/includes/excludes/ExcludedClass" };
		Writer.from(SRC_APP_JAR, SRC_LIB_JAR).excludes(excludedClasses).copyTo(DST_APP_JAR, DST_LIB_JAR);

		Set<String> expectedClassesApp = Reader.from(SRC_APP_JAR).excludes(excludedClasses).listClasses();
		Set<String> actualClassesApp = Reader.from(DST_APP_JAR).listClasses();
		Assert.assertArrayEquals(expectedClassesApp.toArray(), actualClassesApp.toArray());

		Set<String> expectedClassesLib = Reader.from(SRC_LIB_JAR).excludes(excludedClasses).listClasses();
		Set<String> actualClassesLib = Reader.from(DST_LIB_JAR).listClasses();
		Assert.assertArrayEquals(expectedClassesLib.toArray(), actualClassesLib.toArray());
	}

	// special cases

	@Test(expected = FileNotFoundException.class)
	public void testCopyToDirectoryWithSameNameAsFile() throws Exception {
		new File("target/file").createNewFile();
		Writer.from(SRC_APP_JAR).copyTo("target/file");
	}

	@Test
	public void testJarRewriting() throws Exception {
		Writer.from(SRC_APP_JAR).copyTo(DST_DIR);
		Writer.from(SRC_APP_JAR).copyTo(DST_DIR);
	}
}
