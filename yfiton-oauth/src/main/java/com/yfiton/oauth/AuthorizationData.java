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
import java.util.UUID;

/**
 * @author lpellegr
 */
public final class AuthorizationData extends Data {

    private final String authorizationCode;

    public AuthorizationData() {
        this(UUID.randomUUID().toString());
    }

    public AuthorizationData(String authorizationCode) {
        this(authorizationCode, ImmutableMap.of());
    }

    public AuthorizationData(String authorizationCode, Map<String, String> data) {
        super(data);

        Objects.nonNull(authorizationCode);
        this.authorizationCode = authorizationCode;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        AuthorizationData that = (AuthorizationData) obj;

        return Objects.equals(authorizationCode, that.authorizationCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.data, authorizationCode);
    }

}
