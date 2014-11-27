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
import java.util.Map.Entry;
import java.util.Set;

/**
 * Reporter.
 * 
 * @author Tomas Rohovsky
 */
public class Reporter {

	/**
	 * Reports classes with their dependencies to stdout.
	 * 
	 * @param classesWithDependencies
	 */
	public static void report(Map<String, Set<String>> classesWithDependencies) {
		for (Entry<String, Set<String>> entry : classesWithDependencies.entrySet()) {
			System.out.println(entry.getKey());
			for (String dependency : entry.getValue()) {
				System.out.println("\t" + dependency);
			}
		}
	}

	/**
	 * Reports dependencies to stdout.
	 * 
	 * @param dependencies
	 */
	public static void report(Set<String> dependencies) {
		for (String dependency : dependencies) {
			System.out.println(dependency);
		}
	}
}
