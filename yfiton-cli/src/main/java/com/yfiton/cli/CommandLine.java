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

package com.yfiton.cli;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableList;

import java.util.*;

/**
 * Define options available from yfiton utility command.
 *
 * @author lpellegr
 */
public class CommandLine {

    @Parameter(names = {"--notify-with", "--notify-on", "--notify-by", "-n"}, description = "Notifier to use for sending notification")
    public String notifier = "beep";

    @DynamicParameter(names = {"-P"}, description = "Define a parameter to set for the notifier that is used")
    public Map<String, String> parameters = new HashMap<>();

    @Parameter(names = {"--log-level", "-ll"}, description = "Define log level information to display [error, warn, info, debug, trace]")
    public String logLevel = "info";

    @Parameter(names = {"--describe-notifier", "-dn"}, description = "Describe notifier parameters identified by the specified key. Available notifiers can be listed with -ln option in order to know which key to use")
    public String describeNotifier;

    @Parameter(names = {"--list-notifiers", "-ln"}, description = "List available notifiers")
    public boolean listNotifiers;

    @Parameter(names = {"--help", "-h"}, description = "Display help", help = true)
    public boolean help;

    @Parameter(names = {"-X"}, description = "Display stack-traces if an error occurs", hidden = true)
    public boolean displayStackTraces;

    @Parameter(names = {"--version", "-v"}, description = "Display version number")
    public boolean displayVersion;

    @Parameter(names = {"--headless"}, description = "Force the app to operate without graphical user interface for getting authorization from third-party services")
    public boolean headless;

    @Parameter(names = {"--wipe-cache", "-wc"}, description = "Wipe cache folder (it includes authorizations got to access third party services with available notifiers)")
    public boolean wipeCache;

    @Parameter
    public List<String> main = new ArrayList<>(0);

}
