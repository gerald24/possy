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
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "possy")
@Validated
public class PossyProperties {

    private Service service = new Service();
    private PdfGenerator pdfGenerator = new PdfGenerator();

    public Service getService() {
        return service;
    }

    public PdfGenerator getPdfGenerator() {
        return pdfGenerator;
    }

    public static class Service {

        private String url;
        private String username;
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(final String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(final String password) {
            this.password = password;
        }
    }

    public static class PdfGenerator {

        private boolean redirectToFile;
        private Fonts fonts = new Fonts();
        private Pages pages = new Pages();

        public boolean isRedirectToFile() {
            return redirectToFile;
        }

        public void setRedirectToFile(final boolean redirectToFile) {
            this.redirectToFile = redirectToFile;
        }

        public Fonts getFonts() {
            return fonts;
        }

        public Pages getPages() {
            return pages;
        }

        public static class Fonts {

            private FontSpec header = new FontSpec();
            private FontSpec content = new FontSpec();
            private FontSpec footer = new FontSpec();

            public FontSpec getHeader() {
                return header;
            }

            public FontSpec getContent() {
                return content;
            }

            public FontSpec getFooter() {
                return footer;
            }

            public static class FontSpec {

                private String font;
                private int size;
                private int lineHeight;


                public String getFont() {
                    return font;
                }

                public void setFont(final String font) {
                    this.font = font;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(final int size) {
                    this.size = size;
                }

                public int getLineHeight() {
                    return lineHeight;
                }

                public void setLineHeight(final int lineHeight) {
                    this.lineHeight = lineHeight;
                }
            }

        }

        public static class Pages {

            private PageSpec story = new PageSpec();
            private PageSpec task = new PageSpec();
            private PageSpec freeform = new PageSpec();
            private PageSpec bug = new PageSpec();

            public PageSpec getStory() {
                return story;
            }

            public PageSpec getTask() {
                return task;
            }

            public PageSpec getFreeform() {
                return freeform;
            }

            public PageSpec getBug() {
                return bug;
            }

            public static class PageSpec {

                private float width;
                private float height;
                private float border;
                private float margin;

                public float getWidth() {
                    return width;
                }

                public void setWidth(final float width) {
                    this.width = width;
                }

                public float getHeight() {
                    return height;
                }

                public void setHeight(final float height) {
                    this.height = height;
                }

                public float getBorder() {
                    return border;
                }

                public void setBorder(final float border) {
                    this.border = border;
                }

                public float getMargin() {
                    return margin;
                }

                public void setMargin(final float margin) {
                    this.margin = margin;
                }
            }
        }
    }
}
