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

package com.yfiton.api.exceptions;

/**
 * Exception thrown if a problem occurs while converting a parameter value
 * using a {@link com.yfiton.api.parameter.converters.Converter} instance.
 *
 * @author lpellegr
 */
public class ConversionException extends ParameterException {

    private final String resolution;

    public ConversionException(String parameterName, String parameterValue, Class<?> expectedValueType) {
        this("Invalid value for parameter '" + parameterName + "'",
                expectedValueType.getSimpleName() + " value expected but received '" + parameterValue + "'");
    }

    private ConversionException(String message, String resolution) {
        super(message);
        this.resolution = resolution;
    }

    /**
     * Returns a message that explains how to fix the conversion error that has been encountered.
     *
     * @return a message that explains how to fix the conversion error that has been encountered.
     */
    public String getResolution() {
        return resolution;
    }

}
