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

import net.g24.possy.daemon.configuration.PossyProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Component
public class PossyDaemon {

    private static final String GET_URL = "%s/api/print";
    private static final String DELETE_URL = "%s/api/print/%s";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String url;
    private final RestTemplate restTemplate;
    private final PossyService possyService;

    @Autowired
    public PossyDaemon(
            PossyProperties possyProperties, RestTemplate restTemplate, PossyService possyService) {
        this.url = possyProperties.getService().getUrl();
        this.restTemplate = restTemplate;
        this.possyService = possyService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkForPrint() throws Exception {
        logger.trace("scheduled check");

        PrintRequest[] printRequests = restTemplate.getForObject(String.format(GET_URL, url), PrintRequest[].class);
        if (printRequests == null || printRequests.length == 0) {
            return;
        }

        Arrays.stream(printRequests).forEach(printRequest -> {
            try {
                logger.info("processing {}", printRequest);
                possyService.print(printRequest);
            } catch (Exception e) {
                logger.error("Error while printing " + printRequest, e);
            }
            restTemplate.delete(String.format(DELETE_URL, url, printRequest.getId().toString()));
        });
    }

}
