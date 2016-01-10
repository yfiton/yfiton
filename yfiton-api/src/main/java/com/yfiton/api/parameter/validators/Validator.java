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


import com.yfiton.api.exceptions.ValidationException;

/**
 * @author lpellegr
 */
public interface Validator<T> {

    /**
     * Check whether the specified parameter {@code value} is valid. If not, a
     * checked exception that contains the cause is thrown.
     *
     * @param parameterName  the name of the parameter to check.
     * @param parameterValue the associated value to check.
     * @throws ValidationException if the parameter value is invalid.
     */
    void validate(String parameterName, T parameterValue) throws ValidationException;

}
