/*
 * Copyright 2020 Johan Dykström
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

package se.dykstrom.javafx.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConversionServiceImplTest {

    private final ConversionServiceImpl conversionService = new ConversionServiceImpl();

    @ParameterizedTest
    @CsvSource({
            "0,10,16,0",
            "255,10,16,ff",
            "ff,16,2,11111111",
            "100,8,10,64",
            "80000000,16,10," + (((long) Integer.MAX_VALUE) + 1),
    })
    void shouldConvertNumber(String number, int fromBase, int toBase, String expected) {
        assertEquals(expected, conversionService.convertNumber(number, fromBase, toBase));
    }
}
