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

package com.yfiton.oauth;

import com.google.common.collect.ImmutableMap;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author lpellegr
 */
public abstract class Data implements Serializable {

    protected final Map<String, String> data;

    public Data() {
        this(ImmutableMap.of());
    }

    public Data(Map<String, String> data) {
        Objects.nonNull(data);
        this.data = data;
    }

    public Set<Map.Entry<String, String>> getData() {
        return data.entrySet();
    }

    public String get(String requestParameterName) {
        return data.get(requestParameterName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        return Objects.equals(data, ((Data) data).data);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

}
