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

package com.yfiton.api.annotation;

import com.yfiton.api.parameter.converters.Converter;
import com.yfiton.api.parameter.validators.NoValidator;
import com.yfiton.api.parameter.validators.Validator;

import java.lang.annotation.*;

/**
 * Parameters are used to configure a {@link com.yfiton.api.Notifier} instance.
 *
 * @author lpellegr
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {

    String name() default "";

    String description() default "";

    Class<? extends Converter> converter() default Converter.class;

    Class<? extends Validator> validator() default NoValidator.class;

    boolean hidden() default false;

    boolean required() default false;

}