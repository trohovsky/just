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
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides utility methods for manipulating with dependencies.
 * 
 * @author Tomas Rohovsky
 */
public final class DependencyUtils {

	private DependencyUtils() {
	}

	/**
	 * Returns a Map containing the intersection of the given Map and Set.
	 * 
	 * @param map
	 *            the map, must not be null
	 * @param set
	 *            the set, must not be null
	 * @return the intersection of the Map and Set
	 */
	public static Map<String, Set<String>> intersection(final Map<String, Set<String>> map, final Set<String> set) {
		final Map<String, Set<String>> intersection = new TreeMap<String, Set<String>>();

		for (Entry<String, Set<String>> entry : map.entrySet()) {
			for (String setElement : set) {
				if (entry.getValue().contains(setElement)) {

					// adding of the set element to the existing entry
					if (intersection.containsKey(entry.getKey())) {
						intersection.get(entry.getKey()).add(setElement);
					} else {
						// creating of a new set
						final Set<String> newSet = new TreeSet<String>();
						newSet.add(setElement);
						intersection.put(entry.getKey(), newSet);
					}
				}
			}
		}

		return intersection;
	}

	/**
	 * Returns a Set containing the intersection of the given Sets.
	 * 
	 * @param setA
	 *            the first set, must not be null
	 * @param setB
	 *            the second set, must not be null
	 * @return the intersection of the Sets
	 */
	public static Set<String> intersection(final Set<String> setA, final Set<String> setB) {
		final Set<String> intersection = new TreeSet<String>(setA);
		intersection.retainAll(setB);
		return intersection;
	}

	/**
	 * Returns a new Set containing set - map.
	 * 
	 * @param set
	 *            the set, must not be null
	 * @param map
	 *            the map, must not be null
	 * @return the Set containing set - map
	 */
	public static Set<String> subtract(final Set<String> set, final Map<String, Set<String>> map) {
		return subtract(set, flatten(map));
	}

	/**
	 * Returns a new Set containing setA - setB.
	 * 
	 * @param setA
	 *            the set, must not be null
	 * @param setB
	 *            the set, must not be null
	 * @return the Set containing setA - setB
	 */
	public static Set<String> subtract(final Set<String> setA, final Set<String> setB) {
		final Set<String> aMinusB = new TreeSet<String>(setA);
		aMinusB.removeAll(intersection(setB, setA));
		return aMinusB;
	}

	/**
	 * Flattens the Map to a set.
	 * 
	 * @param map
	 *            the map to be flatten
	 * @return the set created by flattening of the map
	 */
	public static Set<String> flatten(final Map<String, Set<String>> map) {
		final Set<String> flattenedMap = new TreeSet<String>();
		for (Entry<String, Set<String>> entry : map.entrySet()) {
			flattenedMap.addAll(entry.getValue());
		}
		return flattenedMap;
	}

	/**
	 * Transforms the class name to the package name.
	 * 
	 * @param className
	 *            the class name
	 * @return the package name
	 */
	public static String getPackageName(final String className) {
		final int endOfPackageName = className.lastIndexOf('/');
		return className.substring(0, endOfPackageName != -1 ? endOfPackageName : 0);
	}

	/**
	 * Transforms the Set of class names to a set of package names.
	 * 
	 * @param classNames
	 *            the Set of class names
	 * @return the Set of packages
	 */
	public static Set<String> toPackageNames(final Set<String> classNames) {
		final Set<String> packageNames = new TreeSet<String>();
		for (String className : classNames) {
			final String packageName = getPackageName(className);
			packageNames.add(packageName);
		}
		return packageNames;
	}

	/**
	 * Transforms the Map containing class names as keys and Sets of class names
	 * as values to a Map containing package names.
	 * 
	 * @param map
	 *            the Map containing class names as keys and Sets of class names
	 *            as values
	 * @return the Map of packages and their depended packages
	 */
	public static Map<String, Set<String>> toPackageNames(final Map<String, Set<String>> map) {
		final Map<String, Set<String>> mapWithPackages = new TreeMap<String, Set<String>>();
		for (Entry<String, Set<String>> entry : map.entrySet()) {
			final String key = getPackageName(entry.getKey());
			final Set<String> values = toPackageNames(entry.getValue());

			// adding of the package names to the existing entry
			if (mapWithPackages.containsKey(key)) {
				mapWithPackages.get(key).addAll(values);
			} else {
				// creating of a new entry
				mapWithPackages.put(key, values);
			}
		}
		return mapWithPackages;
	}
}
