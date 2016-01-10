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

package com.yfiton.api.parameter.validators;

import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.api.exceptions.ValidationException;
import org.junit.Test;

/**
 * Unit tests associated to {@link IntegerValidator}.
 *
 * @author lpellegr
 */
public class IntegerValidatorTest extends ValidatorTest<IntegerValidator> {

    public IntegerValidatorTest() {
        super(IntegerValidator.class);
    }

    @Test
    public void testValidateAcceptAllIntegersValidInput() throws ValidationException {
        getInstance().validate("param", 42);
    }

    @Test(expected = NullPointerException.class)
    public void testValidateAcceptAllIntegersInvalidInput() throws ValidationException {
        getInstance().validate("param", null);
    }

    @Test(expected = ParameterException.class)
    public void testValidateGreaterThanOrEqualsInvalidInput() throws ValidationException {
        new IntegerValidator(7).validate("param", 6);
    }

    @Test
    public void testValidateGreaterThanOrEqualsValidInput1() throws ValidationException {
        new IntegerValidator(7).validate("param", 7);
    }

    @Test
    public void testValidateGreaterThanOrEqualsValidInput2() throws ValidationException {
        new IntegerValidator(7).validate("param", 42);
    }

    @Test(expected = ParameterException.class)
    public void testValidateBoundedInvalidInput1() throws ValidationException {
        new IntegerValidator(7, 10).validate("param", 6);
    }

    @Test(expected = ParameterException.class)
    public void testValidateBoundedInvalidInput2() throws ValidationException {
        new IntegerValidator(7, 10).validate("param", 11);
    }

    @Test
    public void testValidateBoundedValidInput1() throws ValidationException {
        new IntegerValidator(7, 10).validate("param", 7);
    }

    @Test
    public void testValidateBoundedValidInput2() throws ValidationException {
        new IntegerValidator(7, 10).validate("param", 10);
    }

}