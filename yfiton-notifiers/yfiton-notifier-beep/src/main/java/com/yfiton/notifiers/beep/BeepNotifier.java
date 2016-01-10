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

package com.yfiton.notifiers.beep;

import com.yfiton.api.Notifier;
import com.yfiton.api.annotation.Parameter;
import com.yfiton.api.exceptions.NotificationException;
import com.yfiton.api.exceptions.ValidationException;
import com.yfiton.api.parameter.Parameters;
import com.yfiton.api.parameter.validators.IntegerValidator;
import com.yfiton.api.parameter.validators.Validator;

import java.util.Optional;

/**
 * @author lpellegr
 */
public class BeepNotifier extends Notifier {

    private static final int BEEP_PERIOD = 110;

    @Parameter(description = "Time to wait in milliseconds between each repetition", validator = DelayValidator.class)
    private int delay = 300; // in ms

    @Parameter(description = "Play the specified pattern. Asteriks are used to denote beeps while each space sleep for the configured delay", validator = PatternValidator.class)
    private String pattern = "*";

    @Override
    protected Check checkParameters(Parameters parameters) {
        return Check.succeeded();
    }

    @Override
    protected void notify(Parameters parameters) throws NotificationException {
        for (int j = 0; j < pattern.length() - 1; j++) {
            beepOrSleep(j);
        }

        if (pattern.charAt(pattern.length() - 1) == '*') {
            beep();
        }
    }

    private void beepOrSleep(int j) {
        if (pattern.charAt(j) == '*') {
            beep();
            sleep(BEEP_PERIOD);
        } else {
            sleep(delay);
        }
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void beep() {
        System.out.print("\007");
        System.out.flush();
    }

    @Override
    public String getKey() {
        return "beep";
    }

    @Override
    public String getName() {
        return "The well known bell";
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Trigger beeps on the host using the default speaker.");
    }

    @Override
    public Optional<String> getUrl() {
        return Optional.empty();
    }

    public static final class PatternValidator implements Validator<String> {

        @Override
        public void validate(String parameterName, String parameterValue) throws ValidationException {
            for (int i = 0; i < parameterValue.length(); i++) {
                char c = parameterValue.charAt(i);

                if (c != '*' && c != ' ') {
                    throw new ValidationException("Illegal character '" + c + "' detected in pattern '" + parameterValue + "'");
                }
            }
        }

    }

    protected static final class DelayValidator extends IntegerValidator {

        public DelayValidator() {
            super(20);
        }

    }

}
