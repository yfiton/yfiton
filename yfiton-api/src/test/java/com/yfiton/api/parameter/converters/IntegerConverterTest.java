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

import com.yfiton.api.exceptions.ConversionException;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit tests associated to {@link IntegerConverter}.
 *
 * @author lpellegr
 */
public class IntegerConverterTest extends ConverterTest<Integer> {

    public IntegerConverterTest() {
        super(IntegerConverter.class);
    }

    @Test(expected = ConversionException.class)
    public void testConvertInvalidInput() throws ConversionException {
        testConvert("param", "a");
    }

    @Test
    public void testConvertZero() throws ConversionException {
        assertThat(testConvert("param", "0")).isEqualTo(0);
    }

    @Test
    public void testConvertMaxIntegerValue() throws ConversionException {
        assertThat(testConvert("param", Integer.toString(Integer.MAX_VALUE))).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void testConvertMinIntegerValue() throws ConversionException {
        assertThat(testConvert("param", Integer.toString(Integer.MIN_VALUE))).isEqualTo(Integer.MIN_VALUE);
    }

    @Test(expected = ConversionException.class)
    public void testConvertDouble() throws ConversionException {
        assertThat(testConvert("param", "3.1415"));
    }

}