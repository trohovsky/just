package com.github.trohovsky.just.test.app;

import com.github.trohovsky.just.test.lib.annotation.AnnotationForAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationForArrayAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithAnnotation;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithArray;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithClass;
import com.github.trohovsky.just.test.lib.annotation.AnnotationWithEnum;
import com.github.trohovsky.just.test.lib.annotation.ClassForAnnotation;
import com.github.trohovsky.just.test.lib.annotation.EnumForAnnotation;
import com.github.trohovsky.just.test.lib.classtype.Field;
import com.github.trohovsky.just.test.lib.classtype.FieldArray;
import com.github.trohovsky.just.test.lib.classtype.Interface;
import com.github.trohovsky.just.test.lib.classtype.MethodArgument;
import com.github.trohovsky.just.test.lib.classtype.MethodException;
import com.github.trohovsky.just.test.lib.classtype.MethodReturnType;
import com.github.trohovsky.just.test.lib.classtype.SuperClass;
import com.github.trohovsky.just.test.lib.typeparameter.ClassTypeParameter;
import com.github.trohovsky.just.test.lib.typeparameter.FieldTypeParameter;
import com.github.trohovsky.just.test.lib.typeparameter.InterfaceTypeParameter;
import com.github.trohovsky.just.test.lib.typeparameter.MethodTypeParameter;
import com.github.trohovsky.just.test.lib.typeparameter.SuperClassTypeParameter;

@AnnotationWithAnnotation(@AnnotationForAnnotation)
@AnnotationWithArray({ @AnnotationForArrayAnnotation })
@AnnotationWithClass(ClassForAnnotation.class)
@AnnotationWithEnum(EnumForAnnotation.ITEM)
public class ClassDependencies<CTP extends ClassTypeParameter> extends SuperClass<SuperClassTypeParameter> implements Interface<InterfaceTypeParameter> {

	Field<FieldTypeParameter> field;
	FieldArray[] fieldArray;

	public <MTP extends MethodTypeParameter> MethodReturnType method(MethodArgument argument) throws MethodException {
		return null;
	}
}
