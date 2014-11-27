package com.github.trohovsky.just.test.app;

import com.github.trohovsky.just.test.lib.annotation.AnnotationForAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationForArrayAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithArray;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithClass;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithEnum;
import com.github.trohovsky.just.test.lib.annotation.ClassForAnnotation;
import com.github.trohovsky.just.test.lib.annotation.EnumForAnnotation;
import com.github.trohovsky.just.test.lib.method.LocalVariable;
import com.github.trohovsky.just.test.lib.method.LocalVariableAnnotation;
import com.github.trohovsky.just.test.lib.method.LocalVariableArray;
import com.github.trohovsky.just.test.lib.method.MultiANewArray;
import com.github.trohovsky.just.test.lib.method.ParameterAnnotation;
import com.github.trohovsky.just.test.lib.method.TryCatchException;
import com.github.trohovsky.just.test.lib.method.TryCatchExceptionChild;
import com.github.trohovsky.just.test.lib.typeparameter.LocalVariableTypeParameter;

public class MethodDependencies extends MethodDependenciesSuperClass {

	@AnnotationWithAnnotation(@AnnotationForAnnotation)
	@AnnotationWithArray({ @AnnotationForArrayAnnotation })
	@AnnotationWithClass(ClassForAnnotation.class)
	@AnnotationWithEnum(EnumForAnnotation.ITEM)
	public void method(@ParameterAnnotation int i) {

		// local variables
		// they have to be initialized, they are removed if not
		@SuppressWarnings("unused")
		@LocalVariableAnnotation
		LocalVariable<LocalVariableTypeParameter> mv = null;
		@SuppressWarnings("unused")
		LocalVariableArray[] mva = null;

		if (new MultiANewArray[1][1] != null) {
		}

		// field instruction
		fieldInstruction.getValue();

		// load constant
		System.out.println(CONSTANT);

		// try catch block
		try {
			throw new TryCatchExceptionChild();
		} catch (TryCatchException e) {
		}
	}
}
