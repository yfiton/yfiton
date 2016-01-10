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

/**
 * @author lpellegr
 */
public final class CharacterConverter implements Converter<Character> {

    private static final class LazyHolder {

        private static final CharacterConverter INSTANCE = new CharacterConverter();

    }

    protected CharacterConverter() {

    }

    @Override
    public Character convert(String parameterName, String parameterValue) throws ConversionException {
        if (parameterValue.length() > 1) {
            throw new ConversionException(parameterName, parameterValue, Character.class);
        }

        return parameterValue.charAt(0);
    }

    public static CharacterConverter getInstance() {
        return LazyHolder.INSTANCE;
    }

}
