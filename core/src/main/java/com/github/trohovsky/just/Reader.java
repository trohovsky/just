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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.signature.SignatureVisitor;

import com.github.trohovsky.just.model.Dependencies;
import com.github.trohovsky.just.util.Validation;
import com.github.trohovsky.just.visitor.AnnotationDependenciesVisitor;
import com.github.trohovsky.just.visitor.ClassDependenciesVisitor;
import com.github.trohovsky.just.visitor.FieldDependenciesVisitor;
import com.github.trohovsky.just.visitor.MethodDependenciesVisitor;
import com.github.trohovsky.just.visitor.SignatureDependenciesVisitor;

/**
 * Reader of classes and their dependencies.
 * 
 * @author Tomas Rohovsky
 */
public final class Reader {

	private static final String CLASS_EXTENSION = ".class";
	private String[] paths;
	private String[] includes;
	private String[] excludes;

	private Reader(String... paths) {
		this.paths = paths;
	}

	/**
	 * Creates a reader for reading from the specified paths that could refer to
	 * directories or JARs.
	 * 
	 * @param paths
	 *            the paths referring to the directories or JARs
	 * @return the same instance of Reader
	 */
	public static Reader from(final String... paths) {
		Validation.noNullValues(paths, "Paths cannot contain null values");
		return new Reader(paths);
	}

	/**
	 * Sets including prefixes. Classes matched by the prefixes will be included
	 * for reading. All classes are included by default.
	 * 
	 * @param includes
	 *            the including prefixes
	 * @return the same instance of Reader
	 */
	public Reader includes(final String... includes) {
		this.includes = includes;
		return this;
	}

	/**
	 * Sets excluding prefixes. Classes matched by the prefixes will be excluded
	 * from reading. No classes are excluded by default.
	 * 
	 * @param excludes
	 *            the excluding prefixes
	 * @return the same instance of Reader
	 */
	public Reader excludes(final String... excludes) {
		this.excludes = excludes;
		return this;
	}

	/**
	 * Returns Set of classes contained in the encapsulated directories/JARs.
	 * 
	 * @return the Set of classes
	 * @throws IOException
	 */
	public Set<String> listClasses() throws IOException {
		final Set<String> classes = new TreeSet<String>();
		for (String path : paths) {
			final Set<String> classesFromPath = listClasses(path);
			if (classesFromPath != null) {
				classes.addAll(classesFromPath);
			}
		}
		return classes;
	}

	private Set<String> listClasses(final String path) throws IOException {
		Validation.notNull(path, "Path must be specified");

		final File file = new File(path);
		if (file.isDirectory()) {
			return listClassesFromDirInit(file);
		} else {
			return listClassesFromJar(path);
		}
	}

	private Set<String> listClassesFromDirInit(final File dir) {
		final Set<String> classes = new TreeSet<String>();
		listClassesFromDir(dir, "", classes);
		return classes;
	}

	private void listClassesFromDir(final File dir, final String path, final Set<String> classes) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				listClassesFromDir(file, path + file.getName() + "/", classes);
			} else {
				String className = path + removeClassExtension(file.getName());
				if (file.getName().endsWith(CLASS_EXTENSION) && (includes == null || matches(className, includes))
						&& (excludes == null || !matches(className, excludes))) {
					classes.add(className);
				}
			}
		}
	}

	private Set<String> listClassesFromJar(final String path) throws IOException {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		final Set<String> classes = new TreeSet<String>();
		try {
			fis = new FileInputStream(path);
			zis = new ZipInputStream(fis);
			ZipEntry entry = null;

			if (includes != null || excludes != null) {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)
							&& (includes == null || matches(entry.getName(), includes))
							&& (excludes == null || !matches(entry.getName(), excludes))) {
						classes.add(removeClassExtension(entry.getName()));
					}
				}
			} else {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)) {
						classes.add(removeClassExtension(entry.getName()));
					}
				}
			}
			return classes;
		} finally {
			if (zis != null) {
				zis.close();
			}
		}
	}

	/**
	 * Reads dependencies and returns them in a Map where keys are classes from
	 * the encapsulated directories/JARs and values are their dependencies.
	 * 
	 * @return the Map of classes and their dependencies
	 * @throws IOException
	 */
	public Map<String, Set<String>> readClassesWithDependencies() throws IOException {
		final Map<String, Set<String>> classesWithDependencies = new TreeMap<String, Set<String>>();
		for (String path : paths) {
			final Map<String, Set<String>> classesWithDependenciesFromPath = readClassesWithDependencies(path);
			if (classesWithDependenciesFromPath != null) {
				classesWithDependencies.putAll(classesWithDependenciesFromPath);
			}
		}
		return classesWithDependencies;
	}

	private Map<String, Set<String>> readClassesWithDependencies(final String path) throws IOException {
		Validation.notNull(path, "Path must be specified");

		final File file = new File(path);
		if (file.isDirectory()) {
			return listClassesWithDependenciesFromDirInit(file);
		} else {
			return listClassesWithDependenciesFromJar(path);
		}
	}

	private Map<String, Set<String>> listClassesWithDependenciesFromDirInit(final File dir) throws IOException {
		final Map<String, Set<String>> classesWithDependencies = new TreeMap<String, Set<String>>();
		listClassesWithDependenciesFromDir(dir, "", classesWithDependencies);
		return classesWithDependencies;
	}

	private void listClassesWithDependenciesFromDir(final File dir, final String path,
			final Map<String, Set<String>> classes) throws FileNotFoundException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				listClassesWithDependenciesFromDir(file, path + file.getName() + "/", classes);
			} else {
				String className = path + removeClassExtension(file.getName());
				if (file.getName().endsWith(CLASS_EXTENSION) && (includes == null || matches(className, includes))
						&& (excludes == null || !matches(className, excludes))) {
					final Dependencies dependencies = new Dependencies();
					visitClass(new FileInputStream(file), dependencies);
					classes.put(className, dependencies.get());
				}
			}
		}
	}

	private Map<String, Set<String>> listClassesWithDependenciesFromJar(final String path) throws IOException {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		final Map<String, Set<String>> classesWithDependencies = new TreeMap<String, Set<String>>();
		try {
			fis = new FileInputStream(path);
			zis = new ZipInputStream(fis);
			ZipEntry entry = null;

			if (includes != null || excludes != null) {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)
							&& (includes == null || matches(entry.getName(), includes))
							&& (excludes == null || !matches(entry.getName(), excludes))) {
						final Dependencies dependencies = new Dependencies();
						visitClass(zis, dependencies);
						classesWithDependencies.put(removeClassExtension(entry.getName()), dependencies.get());
					}
				}
			} else {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)) {
						final Dependencies dependencies = new Dependencies();
						visitClass(zis, dependencies);
						classesWithDependencies.put(removeClassExtension(entry.getName()), dependencies.get());
					}
				}
			}
			return classesWithDependencies;
		} finally {
			if (zis != null) {
				zis.close();
			}
		}
	}

	/**
	 * Reads dependencies from the encapsulated directories/JARs.
	 * 
	 * @return set of dependencies
	 * @throws IOException
	 */
	public Set<String> readDependencies() throws IOException {
		final Set<String> dependencies = new TreeSet<String>();
		for (String path : paths) {
			final Set<String> dependenciesFromPath = readDependencies(path);
			if (dependenciesFromPath != null) {
				dependencies.addAll(dependenciesFromPath);
			}
		}
		return dependencies;
	}

	private Set<String> readDependencies(final String path) throws IOException {
		Validation.notNull(path, "Path must be specified");

		final File file = new File(path);
		if (file.isDirectory()) {
			return readDependenciesFromDirInit(file);
		} else {
			return readDependenciesFromJar(path);
		}
	}

	private Set<String> readDependenciesFromDirInit(final File dir) throws IOException {
		final Set<String> dependencies = new TreeSet<String>();
		readDependenciesFromDir(dir, "", dependencies);
		return dependencies;
	}

	private void readDependenciesFromDir(final File dir, final String path, final Set<String> dependencies)
			throws FileNotFoundException {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				readDependenciesFromDir(file, path + "/" + file.getName(), dependencies);
			} else {
				String className = path + removeClassExtension(file.getName());
				if (file.getName().endsWith(CLASS_EXTENSION) && (includes == null || matches(className, includes))
						&& (excludes == null || !matches(className, excludes))) {
					final Dependencies dependencyContainer = new Dependencies();
					visitClass(new FileInputStream(file), dependencyContainer);
					dependencies.addAll(dependencyContainer.get());
				}
			}
		}
	}

	private Set<String> readDependenciesFromJar(final String path) throws FileNotFoundException, IOException {
		FileInputStream fis = null;
		ZipInputStream zis = null;
		try {
			fis = new FileInputStream(path);
			zis = new ZipInputStream(fis);
			ZipEntry entry = null;
			final Dependencies dependencies = new Dependencies();

			if (includes != null || excludes != null) {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)
							&& (includes == null || matches(entry.getName(), includes))
							&& (excludes == null || !matches(entry.getName(), excludes))) {
						visitClass(zis, dependencies);
					}
				}
			} else {
				while ((entry = zis.getNextEntry()) != null) {
					if (entry.getName().endsWith(CLASS_EXTENSION)) {
						visitClass(zis, dependencies);
					}
				}
			}
			return dependencies.get();
		} finally {
			if (zis != null) {
				zis.close();
			}
		}
	}

	private static void visitClass(final InputStream is, final Dependencies dependencies) {
		final ClassReader classReader;
		try {
			classReader = new ClassReader(is);

			final AnnotationVisitor annotationVisitor = new AnnotationDependenciesVisitor(dependencies);
			final SignatureVisitor signatureVisitor = new SignatureDependenciesVisitor(dependencies);
			final FieldVisitor fieldVisitor = new FieldDependenciesVisitor(dependencies, annotationVisitor);
			final MethodVisitor methodVisitor = new MethodDependenciesVisitor(dependencies, annotationVisitor,
					signatureVisitor);
			final ClassVisitor visitor = new ClassDependenciesVisitor(dependencies, annotationVisitor,
					signatureVisitor, fieldVisitor, methodVisitor);

			classReader.accept(visitor, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String removeClassExtension(final String name) {
		return name.substring(0, name.length() - CLASS_EXTENSION.length());
	}

	private static boolean matches(final String string, final String[] patterns) {
		if (patterns == null) {
			return false;
		}
		for (String pattern : patterns) {
			if (string.startsWith(pattern)) {
				return true;
			}
		}
		return false;
	}
}
