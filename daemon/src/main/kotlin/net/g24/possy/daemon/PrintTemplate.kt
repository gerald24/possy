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
package net.g24.possy.daemon

enum class PrintTemplate(val paper: PrintPaper) {
    BUG(PrintPaper.PINK),
    TASK(PrintPaper.WHITE),
    STORY(PrintPaper.WHITE),
    FREEFORM(PrintPaper.YELLOW),
    IMAGE(PrintPaper.WHITE)
}