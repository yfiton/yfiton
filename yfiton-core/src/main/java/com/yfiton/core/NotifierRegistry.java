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

package com.yfiton.core;

import com.google.common.collect.ImmutableMap;
import com.yfiton.api.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author lpellegr
 */
public final class NotifierRegistry {

    private static final Logger log = LoggerFactory.getLogger(NotifierRegistry.class);

    private final ServiceLoader<Notifier> notifierLoader;

    private Map<String, Notifier> availableNotifiers;

    private static final class LazyHolder {

        private static final NotifierRegistry INSTANCE = new NotifierRegistry();

    }

    public NotifierRegistry() {
        availableNotifiers = new HashMap<>();
        notifierLoader = ServiceLoader.load(Notifier.class);

        reload();
    }

    public Notifier find(String name) {
        return availableNotifiers.get(name);
    }

    public ImmutableMap<String, Notifier> getAvailable() {
        return ImmutableMap.copyOf(availableNotifiers);
    }

    public void register(Notifier notifier) {
        if (availableNotifiers.containsKey(notifier.getKey())) {
            throw new IllegalArgumentException("Notifier with the same key already registered: " + notifier.getKey());
        }

        availableNotifiers.put(notifier.getKey(), notifier);
    }

    public void reload() {
        notifierLoader.reload();

        Iterator<Notifier> iterator = notifierLoader.iterator();

        while (iterator.hasNext()) {
            try {
                register(iterator.next());
            } catch (ServiceConfigurationError e) {
                log.warn(e.getMessage() + ". Missing JavaFX dependency?");
            }
        }
    }

    public static NotifierRegistry getInstance() {
        return LazyHolder.INSTANCE;
    }

}
