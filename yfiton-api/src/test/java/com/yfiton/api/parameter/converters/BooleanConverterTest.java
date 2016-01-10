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
 * Unit tests associated to {@link BooleanConverter}.
 *
 * @author lpellegr
 */
public class BooleanConverterTest extends ConverterTest<Boolean> {

    public BooleanConverterTest() {
        super(BooleanConverter.class);
    }

    @Test
    public void testFalseCamelCase() throws ConversionException {
        assertThat(testConvert("param", "False")).isFalse();
    }

    @Test
    public void testTrueCamelCase() throws ConversionException {
        assertThat(testConvert("param", "True")).isTrue();
    }

    @Test
    public void testFalseLowerCase() throws ConversionException {
        assertThat(testConvert("param", "false")).isFalse();
    }

    @Test
    public void testTrueLowerCase() throws ConversionException {
        assertThat(testConvert("param", "true")).isTrue();
    }

    @Test(expected = ConversionException.class)
    public void testEmpty() throws ConversionException {
        testConvert("param", "");
    }

    @Test(expected = ConversionException.class)
    public void testTrueTrailingSpace() throws ConversionException {
        testConvert("param", "True ");
    }

}
