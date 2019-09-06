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
package net.g24.possy.daemon.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "cups")
@Validated
public class CupsProperties {

    private String host;
    private int port;
    private Printers printers = new Printers();

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public Printers getPrinters() {
        return printers;
    }

    public static class Printers {

        private String white;
        private String pink;
        private String yellow;

        public String getWhite() {
            return white;
        }

        public void setWhite(final String white) {
            this.white = white;
        }

        public String getPink() {
            return pink;
        }

        public void setPink(final String pink) {
            this.pink = pink;
        }

        public String getYellow() {
            return yellow;
        }

        public void setYellow(final String yellow) {
            this.yellow = yellow;
        }
    }

}
