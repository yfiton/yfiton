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

package com.yfiton.notifiers.desktop.validators;

import com.yfiton.api.exceptions.ValidationException;
import com.yfiton.api.parameter.validators.Validator;
import javafx.geometry.Pos;

/**
 * @author lpellegr
 */
public class PosValidator implements Validator<String> {

    @Override
    public void validate(String parameterName, String parameterValue) throws ValidationException {
        try {
            Pos.valueOf(parameterValue);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    "Received invalid value '" + parameterValue + "' for parameter '" +
                            parameterName + "'. Allowed options are " + getAvailableOptions(), e);
        }
    }

    private String getAvailableOptions() {
        StringBuilder result = new StringBuilder();

        Pos[] values = Pos.values();

        for (int i = 0; i < values.length - 1; i++) {
            result.append(values[i].toString());
            result.append(", ");
        }

        result.append(values[values.length - 1].toString());

        return result.toString();
    }

}
