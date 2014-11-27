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
import com.github.trohovsky.just.test.lib.typeparameter.FieldTypeParameter;

public class FieldDependencies {

	@AnnotationWithAnnotation(@AnnotationForAnnotation)
	@AnnotationWithArray({ @AnnotationForArrayAnnotation })
	@AnnotationWithClass(ClassForAnnotation.class)
	@AnnotationWithEnum(EnumForAnnotation.ITEM)
	private Field<FieldTypeParameter> field;

}
