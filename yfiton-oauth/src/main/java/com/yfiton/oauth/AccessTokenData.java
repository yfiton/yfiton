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

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * @author lpellegr
 */
public final class AccessTokenData extends Data {

    private final String accessToken;

    public AccessTokenData() {
        this(UUID.randomUUID().toString(), ImmutableMap.of());
    }

    public AccessTokenData(String accessToken, Map<String, String> data) {
        super(data);

        Objects.nonNull(accessToken);
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
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

        AccessTokenData that = (AccessTokenData) obj;

        return Objects.equals(accessToken, that.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.data, accessToken);
    }

}
