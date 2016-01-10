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

/**
 * Created by lpellegr on 13/06/15.
 */
public class ExecutionResult {

    private final String command;

    private final Stopwatch stopwatch;

    private final String standardOutput;

    private final String errorOutput;

    private final int returnCode;

    public ExecutionResult(String command, Stopwatch stopwatch, String standardOutput, String errorOutput, int returnCode) {
        this.command = command;
        this.stopwatch = stopwatch;
        this.standardOutput = standardOutput;
        this.errorOutput = errorOutput;
        this.returnCode = returnCode;
    }

    public String getCommand() {
        return command;
    }

    public Stopwatch getStopwatch() {
        return stopwatch;
    }

    public String getStandardOutput() {
        return standardOutput;
    }

    public String getErrorOutput() {
        return errorOutput;
    }

    public int getReturnCode() {
        return returnCode;
    }

    @Override
    public String toString() {
        return "ExecutionResult{" +
                "command='" + command + '\'' +
                ", stopwatch=" + stopwatch +
                ", standardOutput='" + standardOutput + '\'' +
                ", errorOutput='" + errorOutput + '\'' +
                ", returnCode=" + returnCode +
                '}';
    }

}
