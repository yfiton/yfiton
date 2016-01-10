/*
 * Copyright 2015 Laurent Pellegrino
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

package com.yfiton.api.annotation;


import com.google.common.collect.ImmutableMap;
import com.yfiton.api.Notifier;
import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.api.parameter.PrimitiveParameter;
import com.yfiton.api.parameter.converters.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class in charge to process annotations used in {@link Notifier} implementations.
 *
 * @author lpellegr
 */
public final class AnnotationProcessor {

    private static Map<String, Converter> DEFAULT_CONVERTERS;

    static {
        ImmutableMap.Builder<String, Converter> builder = ImmutableMap.builder();

        builder.put(boolean.class.getTypeName(), BooleanConverter.getInstance());
        builder.put(double.class.getTypeName(), DoubleConverter.getInstance());
        builder.put(float.class.getTypeName(), FloatConverter.getInstance());
        builder.put(int.class.getTypeName(), IntegerConverter.getInstance());
        builder.put(Boolean.class.getTypeName(), BooleanConverter.getInstance());
        builder.put(Double.class.getTypeName(), DoubleConverter.getInstance());
        builder.put(Float.class.getTypeName(), FloatConverter.getInstance());
        builder.put(Integer.class.getTypeName(), IntegerConverter.getInstance());
        builder.put(List.class.getTypeName(), new ListStringConverter());
        builder.put(String.class.getTypeName(), new StringConverter());

        DEFAULT_CONVERTERS = builder.build();
    }

    private AnnotationProcessor() {

    }

    public static final Map<String, Map<String, com.yfiton.api.parameter.Parameter>> analyze(Collection<Notifier> notifiers) {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor();

        try {
            return annotationProcessor.process(notifiers);
        } catch (IllegalAccessException | InstantiationException | ParameterException e) {
            throw new IllegalStateException(e);
        }
    }

    protected Map<String, Map<String, com.yfiton.api.parameter.Parameter>> process(
            Collection<? extends Notifier> notifiers) throws IllegalAccessException, InstantiationException, ParameterException {
        ImmutableMap.Builder<String, Map<String, com.yfiton.api.parameter.Parameter>> result = ImmutableMap.builder();

        for (Notifier notifier : notifiers) {

            ImmutableMap.Builder<String, com.yfiton.api.parameter.Parameter> builder = ImmutableMap.builder();

            for (Field field : getAllFields(notifier.getClass())) {
                if (field.isAnnotationPresent(Parameter.class)) {
                    Parameter[] parameters = field.getAnnotationsByType(Parameter.class);

                    if (parameters.length > 1) {
                        throw new IllegalStateException("Only one @Parameter annotation per field allowed: " + parameters.length + " detected");
                    }

                    Parameter annotation = parameters[0];

                    String parameterName = annotation.name();
                    if (parameterName.isEmpty()) {
                        parameterName = field.getName();
                    }

                    field.setAccessible(true);

                    com.yfiton.api.parameter.Parameter parameter = null;

                    if (field.getType().isPrimitive()) {
                        parameter = new PrimitiveParameter(
                                parameterName,
                                annotation.description(),
                                field.getType(),
                                field.get(notifier),
                                getConverter(annotation, field.getType().getTypeName()),
                                annotation.validator().newInstance(),
                                annotation.hidden(),
                                annotation.required());
                    } else {
                        parameter = new com.yfiton.api.parameter.Parameter(
                                parameterName,
                                annotation.description(),
                                field.getType(),
                                field.get(notifier),
                                getConverter(annotation, field.getType().getTypeName()),
                                annotation.validator().newInstance(),
                                annotation.hidden(),
                                annotation.required());
                    }

                    builder.put(parameterName, parameter);

                }
            }

            result.put(notifier.getKey(), builder.build());
        }

        return result.build();
    }

    public static List<Field> getAllFields(Class clazz) {
        return getAllFieldsRecursively(clazz, new ArrayList<>());
    }

    private static List<Field> getAllFieldsRecursively(Class clazz, List<Field> fields) {
        Class superClazz = clazz.getSuperclass();

        if (superClazz != null) {
            getAllFieldsRecursively(superClazz, fields);
        }

        for (Field field : clazz.getDeclaredFields()) {
            fields.add(field);
        }

        return fields;
    }

    private Converter<?> getConverter(Parameter annotation, String fieldTypeName) throws InstantiationException, IllegalAccessException, ParameterException {
        // check if default converter is used
        if (annotation.converter() == Converter.class) {
            // try to get a default converter instance based on the parameter type
            Converter<?> converter = DEFAULT_CONVERTERS.get(fieldTypeName);

            if (converter == null) {
                throw new ParameterException("Unsupported object type. You need to define and use your own converter");
                // TODO link to documentation to explain how to do
            }

            return converter;
        }

        // a user defined converter was specified by the user
        // create a a new instance
        return annotation.converter().newInstance();
    }

}