package com.github.trohovsky.just.test.lib.annotation;

public @interface AnnotationWithClass {

	Class<? extends ClassForAnnotation> value();
}
