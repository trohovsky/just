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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 * Dependency analyzer.
 * 
 * @author Tomas Rohovsky
 */
public class Analyzer {

	// inputs
	private Set<String> artifactClasses;
	private Set<String> artifactClassDependencies;
	private Map<String, Map<String, Set<String>>> dependencies;

	// results
	private Map<String, Set<String>> dependenciesWithResolvedClasses;
	private Set<String> resolvedDependencies;
	private Set<String> resolvedClasses;
	private Map<String, Set<String>> dependenciesWithUnresolvedClasses;
	private Set<String> unresolvedDependencies;
	private Set<String> unresolvedClasses;
	private Map<String, Set<String>> dependenciesWithUnusedClasses;
	private Set<String> unusedDependencies;
	private Set<String> unusedClasses;

	private AnalyzerStrategy analyzerStrategy;

	private Analyzer(String artifactPath) throws IOException {
		artifactClasses = Reader.from(artifactPath).listClasses();
		artifactClassDependencies = Reader.from(artifactPath).readDependencies();
	}

	/**
	 * Creates a dependency analyzer for an artifact specified by its path.
	 * 
	 * @param artifactPath
	 *            the path of the artifact
	 * @return a new instance of this class
	 * @throws IOException
	 */
	public static Analyzer forArtifact(String artifactPath) throws IOException {
		return new Analyzer(artifactPath);
	}

	/**
	 * Sets paths of dependency artifacts used for analysis.
	 * 
	 * @param dependencyPaths
	 * @return the same instance of this class
	 * @throws IOException
	 */
	public Analyzer withDependencies(String... dependencyPaths) throws IOException {
		dependencies = buildDependencies(dependencyPaths);
		return this;
	}

	private Map<String, Map<String, Set<String>>> buildDependencies(String... dependencyPaths)
			throws IOException {
		Map<String, Map<String, Set<String>>> dependencyArtifacts = new LinkedHashMap<String, Map<String, Set<String>>>();
		for (String dependencyPath : dependencyPaths) {
			Map<String, Set<String>> classesWithDependencies = Reader.from(dependencyPath)
					.readClassesWithDependencies();
			dependencyArtifacts.put(dependencyPath, classesWithDependencies);
		}
		return dependencyArtifacts;
	}

	// resolved

	/**
	 * Returns a Map where keys are paths of resolved dependency artifacts and values are Sets of resolved classes of
	 * these dependency artifacts.
	 * 
	 * @return the Map of dependency paths and resolved classes
	 */
	public Map<String, Set<String>> getDependenciesWithResolvedClasses() throws IOException {
		if (dependenciesWithResolvedClasses == null) {
			dependenciesWithResolvedClasses = new LinkedHashMap<String, Set<String>>();
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			analyzerStrategy = new DependenciesWithResolvedClassesStrategy();
			analyze(artifactClassDependencies);
		}
		return dependenciesWithResolvedClasses;
	}

	/**
	 * Returns a Set of paths of resolved dependency artifacts containing resolved classes.
	 * 
	 * @return the Set of paths of resolved dependency artifacts containing resolved classes
	 */
	public Set<String> getResolvedDependencies() {
		if (resolvedDependencies == null && dependenciesWithResolvedClasses == null) {
			resolvedDependencies = new LinkedHashSet<String>();
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			analyzerStrategy = new ResolvedDependenciesStrategy();
			analyze(artifactClassDependencies);
		} else {
			if (dependenciesWithResolvedClasses != null) {
				resolvedDependencies = buildSetOfKeys(dependenciesWithResolvedClasses);
			}
		}
		return resolvedDependencies;
	}

	/**
	 * Returns a Set of resolved classes.
	 * 
	 * @return the Set of resolved classes
	 */
	public Set<String> getResolvedClasses() {
		if (resolvedClasses == null) {
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			analyzerStrategy = new ResolvedClassesStrategy();
			analyze(artifactClassDependencies);
		}
		return resolvedClasses;
	}

	// unresolved

	/**
	 * Returns a Map where keys are paths of dependency artifacts and values are Sets of unresolved classes of these
	 * dependency artifacts.
	 * 
	 * @return the Map of dependency paths and unresolved classes
	 */
	public Map<String, Set<String>> getDependenciesWithUnresolvedClasses() {
		if (dependenciesWithUnresolvedClasses == null) {
			// TODO
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			dependenciesWithUnresolvedClasses = new LinkedHashMap<String, Set<String>>();
			analyzerStrategy = new DependenciesWithUnresolvedClassesStrategy();
			analyze(unusedClasses); 
		}
		return dependenciesWithUnresolvedClasses;
	}

	/**
	 * Returns a Set of paths of dependency artifacts that contains unresolved classes.
	 * 
	 * @return the Set of dependency paths that contains unresolved classes
	 */
	public Set<String> getDependenciesContainingUnresolvedClasses() {
		if (unresolvedDependencies == null && dependenciesWithUnresolvedClasses == null) {
			// TODO
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			unresolvedDependencies = new LinkedHashSet<String>();
			analyzerStrategy = new DependenciesContainingUnresolvedClassesStrategy();
			analyze(unusedClasses);
		} else {
			if (dependenciesWithUnresolvedClasses != null) {
				unresolvedDependencies = buildSetOfKeys(dependenciesWithUnresolvedClasses);
			}
		}
		return unresolvedDependencies;
	}

	/**
	 * Returns a Set of unresolved classes.
	 * 
	 * @return the Set of unresolved classes
	 */
	public Set<String> getUnresolvedClasses() {
		if (unresolvedClasses == null) {
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			analyzerStrategy = new UnresolvedClassesStrategy();
			analyze(artifactClassDependencies);
		}
		return unresolvedClasses;
	}

	// unused

	/**
	 * Returns a Map where keys are paths of dependency artifacts and values are Sets of unresolved classes of
	 * these dependency artifacts.
	 * 
	 * @return the Map of dependency paths with unresolved classes
	 */
	public Map<String, Set<String>> getDependenciesWithUnusedClasses() {
		if (dependenciesWithUnusedClasses == null) {
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			dependenciesWithUnusedClasses = buildDependencyArtifactsWithClasses();
			analyzerStrategy = new DependenciesWithUnusedClassesStrategy();
			analyze(artifactClassDependencies);
		}
		return dependenciesWithUnusedClasses;
	}

	private Map<String, Set<String>> buildDependencyArtifactsWithClasses() {
		Map<String, Set<String>> dependencyArtifactsWithClasses = new LinkedHashMap<String, Set<String>>();
		for (Entry<String, Map<String, Set<String>>> entry : dependencies.entrySet()) {
			dependencyArtifactsWithClasses.put(entry.getKey(), new TreeSet<String>(entry.getValue().keySet()));
		}
		return dependencyArtifactsWithClasses;
	}

	/**
	 * Returns a Set of paths of dependency artifacts that contains unused classes.
	 * 
	 * @return the Set of dependency paths that contains unused classes
	 */
	public Set<String> getDependenciesContainingUnusedClasses() {
		if (unusedDependencies == null && dependenciesWithUnusedClasses == null) {
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			unusedDependencies = buildSetOfKeys(dependencies);
			analyzerStrategy = new UnusedDependenciesStrategy();
			analyze(artifactClassDependencies);
		} else {
			if (dependenciesWithUnusedClasses != null) {
				unusedDependencies = buildSetOfKeys(dependenciesWithUnusedClasses);
			}
		}
		return unusedDependencies;
	}

	private Set<String> buildSetOfKeys(Map<String, ?> map) {
		Set<String> set = new LinkedHashSet<String>();
		for (String key : map.keySet()) {
			set.add(key);
		}
		return set;
	}

	/**
	 * Returns a Set of unused classes.
	 * 
	 * @return the Set of unused classes
	 */
	public Set<String> getUnusedClasses() {
		if (unusedClasses == null) {
			resolvedClasses = new TreeSet<String>();
			unresolvedClasses = new TreeSet<String>();
			unusedClasses = buildClasses();
			analyzerStrategy = new UnusedClassesStrategy();
			analyze(artifactClassDependencies);
		}
		return unusedClasses;
	}

	private Set<String> buildClasses() {
		Set<String> classes = new TreeSet<String>();
		for (Entry<String, Map<String, Set<String>>> entry : dependencies.entrySet()) {
			for (String className : entry.getValue().keySet()) {
				classes.add(className);
			}
		}
		return classes;
	}

	// common

	private void analyze(Set<String> classDependencies) {
		Set<String> nextClassDependencies = new LinkedHashSet<String>();
		for (String className : classDependencies) {
			Entry<String, Map<String, Set<String>>> dependency = findDependencyArtifactByClass(className);
			if (dependency != null) {
				analyzerStrategy.modifyResult(dependency.getKey(), className);
				resolvedClasses.add(className);
				// reduce class dependencies for next iteration to not examined yet
				for (String classDependency : dependency.getValue().get(className)) {
					if (!resolvedClasses.contains(classDependency) && !unresolvedClasses.contains(classDependency)
							&& !artifactClasses.contains(classDependency)) {
						nextClassDependencies.add(classDependency);
					}
				}
			} else {
				// class is unresolved
				if (!artifactClasses.contains(className)) {
					unresolvedClasses.add(className);
				}
			}
		}

		// analyze next class dependencies
		if (!nextClassDependencies.isEmpty()) {
			analyze(nextClassDependencies);
		}
	}

	private Entry<String, Map<String, Set<String>>> findDependencyArtifactByClass(String className) {
		for (Entry<String, Map<String, Set<String>>> entry : dependencies.entrySet()) {
			if (entry.getValue().containsKey(className)) {
				return entry;
			}
		}
		return null;
	}

	private void addClassToDependencyArtifact(String className, String artifactPath) {
		Set<String> classNames = dependenciesWithResolvedClasses.get(artifactPath);
		if (classNames == null) {
			TreeSet<String> newClassNames = new TreeSet<String>();
			newClassNames.add(className);
			dependenciesWithResolvedClasses.put(artifactPath, newClassNames);
		} else {
			classNames.add(className);
		}
	}

	private interface AnalyzerStrategy {
		void modifyResult(String dependencyName, String className);
	}

	// resolved

	private class DependenciesWithResolvedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			addClassToDependencyArtifact(className, dependencyName);
		}
	}

	private class ResolvedDependenciesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			resolvedDependencies.add(dependencyName);
		}
	}

	private class ResolvedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			// intentionally left empty
		}
	}

	// unresolved

	private class DependenciesWithUnresolvedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			// TODO
		}
	}

	private class DependenciesContainingUnresolvedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			// TODO
		}
	}

	private class UnresolvedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			// intentionally left empty
		}
	}

	// unused

	private class DependenciesWithUnusedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			dependenciesWithUnusedClasses.get(dependencyName).remove(className);
		}
	}

	private class UnusedDependenciesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			unusedDependencies.remove(dependencyName);
		}
	}

	private class UnusedClassesStrategy implements AnalyzerStrategy {
		public void modifyResult(String dependencyName, String className) {
			unusedClasses.remove(className);
		}
	}

}
