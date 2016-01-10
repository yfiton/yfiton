/*
 * Copyright 2016 Laurent Pellegrino
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

package com.yfiton.api.annotation;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author lpellegr
 */
public class AnnotationProcessorTest {

    @Test
    public void testGetAllFields() throws Exception {
        List<Field> fields =
                AnnotationProcessor.getAllFields(A.class);

        assertThat(fields).hasSize(4);
    }

    @Test
    public void testGetAllFieldsNoField() throws Exception {
        List<Field> fields =
                AnnotationProcessor.getAllFields(D.class);

        assertThat(fields).hasSize(0);
    }

    private static final class A extends B {

        private final String a = "a";

    }

    private static class B extends C {

        protected int b = 0;

    }

    protected static class C extends D {

        public static Object c1 = new Object();

        char c2 = 'c';

    }

    public static class D {

    }

}