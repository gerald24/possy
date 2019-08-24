/*
 * This file is part of possy.
 *
 * possy is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * possy is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with possy. If not, see <http://www.gnu.org/licenses/>.
 */
package net.g24.possy.service.model;

import java.util.stream.Stream;

// TODO (https://github.com/gerald24/possy/issues/5) convert to kotlin

/**
 * @author: Gerald Leeb
 */
public enum PrintTemplate {
    BUG("Pink"),
    TASK("White"),
    STORY("White"),
    IMAGE("White"),
    FREEFORM("Yellow");

    private final String printer;

    PrintTemplate(final String printer) {
        this.printer = printer;
    }

    public String getPrinter() {
        return printer;
    }

    public static PrintTemplate forValue(final String template) {
        return Stream.of(values()).filter(value -> value.name().equalsIgnoreCase(template)).findFirst().orElse(BUG);
    }
}
