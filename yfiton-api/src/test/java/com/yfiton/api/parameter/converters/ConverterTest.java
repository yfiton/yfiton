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

package com.yfiton.api.parameter.converters;

import com.google.common.truth.Truth;
import com.google.common.truth.TruthJUnit;
import com.yfiton.api.exceptions.ConversionException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Base class to write test against a {@link Converter} implementation.
 *
 * @author lpellegr
 */
public abstract class ConverterTest<T> {

    private final Class<? extends Converter<T>> converterClass;

    public ConverterTest(Class<? extends Converter<T>> converterClass) {
        this.converterClass = converterClass;
    }

    public T testConvert(String parameterName, String parameterValue) throws ConversionException {
        try {
            return getConverter().convert(parameterName, parameterValue);
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    private Converter<T> getConverter() throws InstantiationException, IllegalAccessException {
        return converterClass.newInstance();
    }

    @Test
    public void testGetInstance() throws InvocationTargetException, IllegalAccessException {
        try {
            Method method = converterClass.getMethod("getInstance");
            Truth.assertThat(method.invoke(null)).isSameAs(method.invoke(null));
        } catch (NoSuchMethodException e) {
            // ignore test if method does not exist
        }
    }

}
