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
import com.yfiton.api.exceptions.ConversionException;
import org.junit.Test;

/**
 * Unit tests associated to {@link FloatConverter}.
 *
 * @author lpellegr
 */
public class FloatConverterTest extends ConverterTest<Float> {

    public FloatConverterTest() {
        super(FloatConverter.class);
    }

    @Test(expected = ConversionException.class)
    public void testConvertInvalidInput() throws ConversionException {
        testConvert("param", "a");
    }

    @Test
    public void testConvertZero() throws ConversionException {
        Float result = testConvert("param", "0");
        Truth.assertThat(result).isEquivalentAccordingToCompareTo(0f);
    }

    @Test
    public void testConvertDouble() throws ConversionException {
        Float result = testConvert("param", Float.toString(Float.MAX_VALUE));
        Truth.assertThat(result).isEquivalentAccordingToCompareTo(Float.MAX_VALUE);
    }

}