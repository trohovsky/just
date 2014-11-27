package com.github.trohovsky.just.test.app;

import com.github.trohovsky.just.test.lib.method.AnnotationDefault;

public @interface InterfaceMethodDependencies {

	AnnotationDefault value() default @AnnotationDefault;

}
