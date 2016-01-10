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
import com.yfiton.api.parameter.validators.IntegerValidator;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit tests associated to {@link StringConverter}.
 *
 * @author lpellegr
 */
public class StringConverterTest extends ConverterTest<String> {

    public StringConverterTest() {
        super(StringConverter.class);
    }

    @Test
    public void testConvert() throws ConversionException {
        assertThat(testConvert("param", "any")).isEqualTo("any");
    }

}