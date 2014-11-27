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

import org.junit.Assert;

public abstract class AbstractDependenciesTest {

	protected static Map<String, Set<String>> classesWithDependencies;

	static {
		try {
			classesWithDependencies = Reader.from("target/test-app.jar").readClassesWithDependencies();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void resolveUsedClassesAndVerify(String myClass, Set<String> externalClasses) {
		Map<String, Set<String>> intersection = DependencyUtils.intersection(classesWithDependencies, externalClasses);

		Set<String> dependenciesOfClass = intersection.get(myClass);
		Assert.assertNotNull(dependenciesOfClass);
		Assert.assertArrayEquals(externalClasses.toArray(), dependenciesOfClass.toArray());
	}

}