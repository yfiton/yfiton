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
 * Unit tests associated to {@link DoubleConverter}.
 *
 * @author lpellegr
 */
public class DoubleConverterTest extends ConverterTest<Double> {

    public DoubleConverterTest() {
        super(DoubleConverter.class);
    }

    @Test(expected = ConversionException.class)
    public void testConvertInvalidInput() throws ConversionException {
        testConvert("param", "a");
    }

    @Test
    public void testConvertZero() throws ConversionException {
        Double result = testConvert("param", "0");
        Truth.assertThat(result).isWithin(1e-7).of(0);
    }

    @Test
    public void testConvertDouble() throws ConversionException {
        Double result = testConvert("param", Double.toString(Double.MAX_VALUE));
        Truth.assertThat(result).isWithin(1e-7).of(Double.MAX_VALUE);
    }

}