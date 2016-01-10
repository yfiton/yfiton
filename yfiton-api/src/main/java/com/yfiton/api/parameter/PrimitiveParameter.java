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

package com.yfiton.api.parameter;

import com.yfiton.api.parameter.converters.Converter;
import com.yfiton.api.parameter.validators.Validator;

/**
 * @author lpellegr
 */
public final class PrimitiveParameter extends Parameter {

    public PrimitiveParameter(String name, String description, Class type, Object defaultValue, Converter converter, Validator validator, boolean hidden, boolean required) {
        super(name, description, type, defaultValue, converter, validator, hidden, required);
    }

    @Override
    public Object cast(Object value) {
        return value;
    }

}
