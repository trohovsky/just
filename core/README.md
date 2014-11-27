JUST (Java USed Types)
======================

JUST performs static analysis of Java application's class dependencies. Its usage is very simple:

```
Set<String> dependencies = Reader.from("application.jar").readDependencies();
Set<String> externalClasses = Reader.from("library.jar").listClasses();
Set<String> dependenciesFromLibrary = DependencyUtils.intersection(dependencies, externalClasses);
```

The method `Reader.readDependencies` returns a set of dependencies of application.jar's classes, the analysis is done on bytecode level. The method `Reader.listClasses` lists classes contained in the library.jar archive. Both these methods are able to read from directories. The method `DependencyUtils.intersection` determines what classes from library.jar are dependencies of application.jar.

In case that you are interested in dependencies each class depends on then you can use this methods instead:

```
Map<String, Set<String>> classesWithDependencies = Reader.readClassesWithDependencies("application.jar");
```

The method `Reader.readClassesWithDependencies` returns a map where keys are names of the classes from application.jar and values are sets of the dependend classes. If you already have classes with dependencies and you want to get the dependencies only, it is better to transrom the map to a set by the method `DependencyUtils.flattenMap` than to use `Reader.readDependencies` which has to parse the artifact again:

```
Set<String> dependencies = DependencyUtils.flattenMap(usedClasses);
```

`DependencyUtils` also provides methods for resolving of external types that are not used in the artifact:

```
DependencyUtils.subtract(externalTypes, dependencies);
DependencyUtils.subtract(externalTypes, classesWithDependencies);
```
<br/>
JUST is based on ASM library <http://asm.ow2.org> and highly inspired by the article <http://asm.ow2.org/doc/tutorial-asm-2.0.html>

