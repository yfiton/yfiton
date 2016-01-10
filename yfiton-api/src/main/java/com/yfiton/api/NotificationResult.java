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

package com.yfiton.api;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * @author lpellegr
 */
public class NotificationResult {

    private final String notifierKey;

    private final Stopwatch stopwatch;

    public NotificationResult(String notifierKey, Stopwatch stopwatch) {
        this.notifierKey = notifierKey;
        this.stopwatch = stopwatch;
    }

    public long getExecutionTime(TimeUnit timeUnit) {
        return stopwatch.elapsed(timeUnit);
    }

}
