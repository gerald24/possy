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
package net.g24.possy.daemon;

// TODO https://github.com/gerald24/possy/issues/3

/**
 * @author: Gerald Leeb
 */
public enum PrintTemplate {
    BUG(PrintPaper.PINK),
    TASK(PrintPaper.WHITE),
    STORY(PrintPaper.WHITE),
    FREEFORM(PrintPaper.YELLOW),
    IMAGE(PrintPaper.WHITE);

    private final PrintPaper paper;

    PrintTemplate(final PrintPaper paper) {
        this.paper = paper;
    }

    public PrintPaper getPaper() {
        return paper;
    }
}
