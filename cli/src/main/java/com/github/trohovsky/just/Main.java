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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CLI Main.
 * 
 * @author Tomas Rohovsky
 */
public final class Main {

	private static final String HELP_CMDLINE = "just [OPTION]... ARTIFACT... [DEPENDENCY...]";
	private static final String HELP_HEADER = "Analyze used classes in ARTIFACT(s) from its(/their) DEPENDENCY(ies).\n";
	private static final String HELP_FOOTER = "\nTo define multiple arguments for options, artifacts or dependencies "
			+ "use ',' without whitespaces as a separator. Options -di, -de, -u can be applied only if at least one "
			+ "dependency is specified.";

	private Main() {
	}

	public static void main(String[] args) throws IOException {

		// parsing of command line
		final CommandLineParser parser = new GnuParser();

		final Options options = new Options();
		options.addOption("ai", true, "prefixes of classes from artifacts that will be included");
		options.addOption("ae", true, "prefixes of classes from artifacts that will be excluded");
		options.addOption("di", true, "prefixes of classes from dependencies that will be included");
		options.addOption("de", true, "prefixes of classes from dependencies that will be excluded");
		options.addOption("f", "flatten", false, "flatten report, display only used classes");
		options.addOption("p", "packages", false, "display package names instead of class names");
		options.addOption("u", "unused", false, "display unused classes from dependencies");
		options.addOption("h", "help", false, "print this help");

		CommandLine cmdLine = null;
		try {
			cmdLine = parser.parse(options, args);
			if (cmdLine.hasOption('h')) {
				final HelpFormatter formatter = new HelpFormatter();
				formatter.setOptionComparator(null);
				formatter.printHelp(HELP_CMDLINE, HELP_HEADER, options, HELP_FOOTER);
				return;
			}
			if (cmdLine.getArgs().length == 0) {
				throw new ParseException("Missing ARTIFACT and/or DEPENDENCY.");
			} else if (cmdLine.getArgs().length > 2) {
				throw new ParseException(
						"More that two arquments found, multiple ARTIFACTs DEPENDENCies should be separated by ','"
								+ " without whitespaces.");
			}

		} catch (ParseException e) {
			System.err.println("Error parsing command line: " + e.getMessage());
			final HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(HELP_CMDLINE, HELP_HEADER, options, HELP_FOOTER);
			return;
		}

		// obtaining of values
		final String[] artifactPaths = cmdLine.getArgs()[0].split(",");
		final String[] dependencyPaths = cmdLine.getArgs().length == 2 ? cmdLine.getArgs()[1].split(",") : null;
		final String[] artifactIncludes = splitValues(cmdLine.getOptionValue("ai"));
		final String[] artifactExcludes = splitValues(cmdLine.getOptionValue("ae"));
		final String[] dependencyIncludes = splitValues(cmdLine.getOptionValue("di"));
		final String[] dependencyExcludes = splitValues(cmdLine.getOptionValue("de"));

		// validation of values
		if (dependencyPaths == null) {
			if (dependencyIncludes != null) {
				System.err.println("At least one dependency has to be specified to use option -di");
				return;
			}
			if (dependencyExcludes != null) {
				System.err.println("At least one dependency has to be specified to use option -de");
				return;
			}
			if (cmdLine.hasOption('u')) {
				System.err.println("At least one dependency has to be specified to use option -u");
				return;
			}
		}

		// execution
		Set<String> externalClasses = null;
		if (dependencyPaths != null) {
			externalClasses = Reader.from(dependencyPaths).includes(dependencyIncludes).excludes(dependencyExcludes)
					.listClasses();
		}

		if (cmdLine.hasOption('f') || cmdLine.hasOption('u')) {
			Set<String> dependencies = Reader.from(artifactPaths).includes(artifactIncludes).excludes(artifactExcludes)
					.readDependencies();
			if (externalClasses != null) {
				dependencies = DependencyUtils.intersection(dependencies, externalClasses);
				if (cmdLine.hasOption('u')) {
					dependencies = DependencyUtils.subtract(externalClasses, dependencies);
				}
			}
			if (cmdLine.hasOption('p')) {
				dependencies = DependencyUtils.toPackageNames(dependencies);
			}
			Reporter.report(dependencies);
		} else {
			Map<String, Set<String>> classesWithDependencies = Reader.from(artifactPaths).includes(artifactIncludes)
					.excludes(artifactExcludes).readClassesWithDependencies();
			if (externalClasses != null) {
				classesWithDependencies = DependencyUtils.intersection(classesWithDependencies, externalClasses);
			}
			if (cmdLine.hasOption('p')) {
				classesWithDependencies = DependencyUtils.toPackageNames(classesWithDependencies);
			}
			Reporter.report(classesWithDependencies);
		}
	}

	private static String[] splitValues(String text) {
		if (text == null) {
			return null;
		}
		return text.split(",");
	}
}
