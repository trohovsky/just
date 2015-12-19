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

import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

// A1 -> B1 -> C1 -> B3
// A2
// B2 -> C2
// D1
public class AnalyzerTest {

	private static final String A = "target/test-a";
	private static final String B = "target/test-b";
	private static final String C = "target/test-c";
	private static final String D = "target/test-d";

	private static final String B1 = "com/github/trohovsky/just/test/b/B1";
	private static final String B2 = "com/github/trohovsky/just/test/b/B2";
	private static final String B3 = "com/github/trohovsky/just/test/b/B3";
	private static final String C1 = "com/github/trohovsky/just/test/c/C1";
	private static final String C2 = "com/github/trohovsky/just/test/c/C2";
	private static final String D1 = "com/github/trohovsky/just/test/d/D1";

	// resolved

	@Test
	public void testDependenciesWithResolvedClasses() throws Exception {
		String[] expectedDependencies = { B, C };
		Map<String, Set<String>> dependenciesWithResolvedClasses = Analyzer.forArtifact(A).withDependencies(B, C, D)
				.getDependenciesWithResolvedClasses();
		Assert.assertArrayEquals(expectedDependencies, dependenciesWithResolvedClasses.keySet().toArray());

		String[] expectedClassesB = { B1, B3 };
		Set<String> resolvedClassesB = dependenciesWithResolvedClasses.get(B);
		Assert.assertArrayEquals(expectedClassesB, resolvedClassesB.toArray());

		String[] expectedClassesC = { C1 };
		Set<String> resolvedClassesC = dependenciesWithResolvedClasses.get(C);
		Assert.assertArrayEquals(expectedClassesC, resolvedClassesC.toArray());
	}

	@Test
	public void testResolvedDependencies() throws Exception {
		String[] expectedDependencies = { B, C };
		Set<String> resolvedDependencies = Analyzer.forArtifact(A).withDependencies(B, C, D).getResolvedDependencies();
		Assert.assertArrayEquals(expectedDependencies, resolvedDependencies.toArray());
	}

	@Test
	public void testResolvedClasses() throws Exception {
		String[] expectedClasses = { B1, B3, C1 };
		Set<String> resolvedClasses = Analyzer.forArtifact(A).withDependencies(B, C, D).getResolvedClasses();
		Assert.assertArrayEquals(expectedClasses, resolvedClasses.toArray());
	}

	// unresolved

	@Ignore
	@Test
	public void testDependenciesWithUnresolvedClasses() throws Exception {
		// A = {"java/lang/Object"}, B = {C1, "java/lang/Object}
	}

	@Ignore
	@Test
	public void testDependenciesContainingUnresolvedClasses() throws Exception {
		// A, B
	}

	@Test
	public void testUnreslovedClasses() throws Exception {
		String[] expectedClasses = { C1, "java/lang/Object" };
		Set<String> unresolvedClasses = Analyzer.forArtifact(A).withDependencies(B, D).getUnresolvedClasses();
		Assert.assertArrayEquals(expectedClasses, unresolvedClasses.toArray());
	}

	// unused

	@Test
	public void testDependenciesWithUnusedClasses() throws Exception {
		String[] expectedDependencies = { B, C, D };
		Map<String, Set<String>> dependenciesWithUnusedClasses = Analyzer.forArtifact(A).withDependencies(B, C, D)
				.getDependenciesWithUnusedClasses();
		Assert.assertArrayEquals(expectedDependencies, dependenciesWithUnusedClasses.keySet().toArray());

		String[] expectedClassesB = { B2 };
		Set<String> resolvedClassesB = dependenciesWithUnusedClasses.get(B);
		Assert.assertArrayEquals(expectedClassesB, resolvedClassesB.toArray());

		String[] expectedClassesC = { C2 };
		Set<String> resolvedClassesC = dependenciesWithUnusedClasses.get(C);
		Assert.assertArrayEquals(expectedClassesC, resolvedClassesC.toArray());

		String[] expectedClassesD = { D1 };
		Set<String> resolvedClassesD = dependenciesWithUnusedClasses.get(D);
		Assert.assertArrayEquals(expectedClassesD, resolvedClassesD.toArray());
	}

	@Test
	public void testDependenciesContainingUnusedClasses() throws Exception {
		String[] expectedDependencies = { D };
		Set<String> unusedDependencies = Analyzer.forArtifact(A).withDependencies(B, C, D)
				.getDependenciesContainingUnusedClasses();
		Assert.assertArrayEquals(expectedDependencies, unusedDependencies.toArray());
	}

	@Test
	public void testUnusedClasses() throws Exception {
		String[] expectedClasses = { B2, C2, D1 };
		Set<String> unusedClasses = Analyzer.forArtifact(A).withDependencies(B, C, D).getUnusedClasses();
		System.out.println(unusedClasses);
		Assert.assertArrayEquals(expectedClasses, unusedClasses.toArray());
	}

}
