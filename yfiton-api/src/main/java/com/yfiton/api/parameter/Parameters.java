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

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author lpellegr
 */
public class Parameters implements Iterable<Map.Entry<String, ParameterValue>> {

    private Map<String, ParameterValue> parameters;

    public Parameters(Map<String, ParameterValue> parameters) {
        this.parameters = parameters;
    }

    public <T> T getValue(Parameter<T> parameter) {
        ParameterValue parameterValue = parameters.get(parameter.getName());

        if (parameterValue != null) {
            return parameterValue.getValue(parameter);
        }

        return null;
    }

    public <T> boolean contains(String parameterName) {
        return parameters.containsKey(parameterName);
    }

    public Map<String, ParameterValue> nameStartingWith(String prefix) {
        return parameters.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(prefix))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    @Override
    public Iterator<Map.Entry<String, ParameterValue>> iterator() {
        return parameters.entrySet().iterator();
    }

    @Override
    public void forEach(Consumer action) {
        parameters.entrySet().forEach(action);
    }

    @Override
    public Spliterator spliterator() {
        return parameters.entrySet().spliterator();
    }

}
