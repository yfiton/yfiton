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

import com.yfiton.api.exceptions.ConversionException;
import com.yfiton.api.exceptions.ParameterException;
import com.yfiton.api.parameter.converters.Converter;
import com.yfiton.api.parameter.validators.Validator;

/**
 * @author lpellegr
 */
public class Parameter<T> {

    private final String name;

    private final String description;

    private final boolean hidden;

    private final boolean required;

    private final Class<?> type;

    private final Object defaultValue;

    private final Converter<T> converter;

    private final Validator<T> validator;

    public Parameter(String name, String description, Class<?> type, Object defaultValue, Converter<T> converter, Validator<T> validator, boolean hidden, boolean required) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
        this.hidden = hidden;
        this.required = required;
        this.converter = converter;
        this.validator = validator;
    }

    public T cast(Object value) {
        return (T) type.cast(value);
    }

    public void checkValidity(T value) throws ParameterException {
        validator.validate(name, value);
    }

    public T parse(String value) throws ConversionException {
        return converter.convert(name, value);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isRequired() {
        return required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Parameter<?> parameter = (Parameter<?>) o;

        if (hidden != parameter.hidden) return false;
        if (required != parameter.required) return false;
        if (!name.equals(parameter.name)) return false;
        if (!description.equals(parameter.description)) return false;
        if (!type.equals(parameter.type)) return false;
        if (!defaultValue.equals(parameter.defaultValue)) return false;
        if (!converter.equals(parameter.converter)) return false;
        return validator.equals(parameter.validator);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (hidden ? 1 : 0);
        result = 31 * result + (required ? 1 : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + defaultValue.hashCode();
        result = 31 * result + converter.hashCode();
        result = 31 * result + validator.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", hidden=" + hidden +
                ", required=" + required +
                ", type=" + type +
                ", defaultValue=" + defaultValue +
                ", converter=" + converter +
                ", validator=" + validator +
                '}';
    }

}
