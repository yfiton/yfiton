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

/**
 * Unit tests associated to {@link CharacterConverter}.
 *
 * @author lpellegr
 */
public class CharacterConverterTest extends ConverterTest<Character> {

    public CharacterConverterTest() {
        super(CharacterConverter.class);
    }

    @Test
    public void testConvertStringWithSingleCharacter() throws ConversionException {
        testConvert("param", "a");
    }

    @Test(expected = ConversionException.class)
    public void testConvertStringWithTwoCharacters() throws ConversionException {
        testConvert("param", "ab");
    }

    @Test(expected = ConversionException.class)
    public void testConvertStringWithMultipleCharacters() throws ConversionException {
        testConvert("param", "abcdefg");
    }

}