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

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Unit tests associated to {@link ListStringConverter}.
 *
 * @author lpellegr
 */
public class ListStringConverterTest extends ConverterTest<List<String>> {

    public ListStringConverterTest() {
        super(ListStringConverter.class);
    }

    @Test
    public void testConvertEmptyInput() throws ConversionException {
        assertThat(testConvert("param", "")).isEmpty();
    }

    @Test
    public void testConvertSingleElementInput() throws ConversionException {
        List<String> result = testConvert("param", "1");
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly("1");
    }

    @Test
    public void testConvertOneCommaTwoCommaThreeCommaFourInput() throws ConversionException {
        List<String> result = testConvert("param", "1,2,3,4");
        assertThat(result).hasSize(4);
        assertThat(result).containsExactly("1", "2", "3", "4");
    }

    @Test
    public void testConvertCommaInput() throws ConversionException {
        List<String> result = testConvert("param", ",");
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("", "");
    }

    @Test
    public void testConvertMultipleCharactersInput() throws ConversionException {
        List<String> result = testConvert("param", "a#2,@*112,hello,bye!++");
        assertThat(result).hasSize(4);
        assertThat(result).containsExactly("a#2", "@*112", "hello", "bye!++");
    }

    @Test
    public void testConvertCommaCommaInput() throws ConversionException {
        List<String> result = testConvert("param", ",,");
        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("", "", "");
    }

    @Test
    public void testConvertInputEndingWithComma() throws ConversionException {
        List<String> result = testConvert("param", "1,2,3,");
        assertThat(result).hasSize(4);
        assertThat(result).containsExactly("1", "2", "3", "");
    }

    @Test
    public void testConvertInputStartingWithComma() throws ConversionException {
        List<String> result = testConvert("param", ",1,2,3");
        System.out.println("ListStringConverterTest.testConvertInputStartingWithComma" + result);
        assertThat(result).hasSize(4);
        assertThat(result).containsExactly("", "1", "2", "3");
    }

}