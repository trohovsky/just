TODO
----
* CLI
	- consider adding of option for listing of classes in archive -> flatten should be removed and option like -f/-format cpdp/cp/dp/cd/c/d estabilished
* Core
	- http://www.oracle.com/technetwork/articles/java/compress-1565076.html
	- Writer writes archives with 0 size files in case of running ZOS, archives without content visible in case of running FOS
	- file is neither JAR nor directory
	- review Dependencies
 	- implement Writer class for writing of shrinked dependencies [NEXT RELEASE]
	- checking for presence of constructions like Class.newInstance(),Class.fromClass() [NEXT RELEASE]
	- consider dependencyIncludes for readClassDependencies/readDependencies methods
	- consider using of pretty names
* Testing
	- tests for DependencyUtils [OPTIONAL]
	- tests for reading from directory [OPTIONAL]
	- consider using of MyClass.class.getName() instead of error prone string constants [OPTIONAL]
* Release
	- artifact name, package names
	- API docs
		- articles a/the
		- formatting of types (Map, Set)
	- release to maven repo
	- add checkstyle
	- add findbugs

Usage in other projects/use cases
---------------------------------
* use in japi-checker to check only API changes a user is interest
* Maven plugin for analysis of used/unused dependencies (competing with dependency:analyze)
* Maven plugin that provides safe upgre of dependencies (versions-maven-plugin extenssion)
* reducing JavaArchives in ShrinkWrap, useful when using Maven Resolver and having multiple transitive dependencies
* reducing uber JARs (maven-shade-plugin, maven-assembly-plugin)
* finding of import packages for OSGI bundles
