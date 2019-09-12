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

package net.g24.possy.service;

import com.vaadin.flow.server.PWA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * A reflection hack to modify the values of Vaadin's {@link PWA} annotation dynamically.
 * Cannot use Kotlin here, because the language doesn't allow instantiating annotation types.
 */
class PwaAnnotationModifier {

	private PwaAnnotationModifier() {
		// no instantiation
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PwaAnnotationModifier.class);

	private static final String ANNOTATIONS = "annotations";
	private static final String ANNOTATION_DATA = "annotationData";

	/**
	 * Important: Needs to be called before {@link com.vaadin.flow.server.PwaRegistry} get's instantiated.
	 */
	static void dynamicPwaAnnotation(final Class<?> classWithPwaAnnotation, final String newShortName, final String newName) {
		PWA oldAnnotation = classWithPwaAnnotation.getDeclaredAnnotation(PWA.class);
		LOGGER.info("old PWA: shortName = {}, name = {}", oldAnnotation.shortName(), oldAnnotation.name());

		Annotation newPwa = new PWA() {

			@Override
			public String offlinePath() {
				return oldAnnotation.offlinePath();
			}

			@Override
			public String manifestPath() {
				return oldAnnotation.manifestPath();
			}

			@Override
			public String iconPath() {
				return oldAnnotation.iconPath();
			}

			@Override
			public String startPath() {
				return oldAnnotation.startPath();
			}

			@Override
			public String name() {
				return newName;
			}

			@Override
			public String shortName() {
				return newShortName;
			}

			@Override
			public String description() {
				return oldAnnotation.description();
			}

			@Override
			public String themeColor() {
				return oldAnnotation.themeColor();
			}

			@Override
			public String backgroundColor() {
				return oldAnnotation.backgroundColor();
			}

			@Override
			public String display() {
				return oldAnnotation.display();
			}

			@Override
			public String[] offlineResources() {
				return oldAnnotation.offlineResources();
			}

			@Override
			public boolean enableInstallPrompt() {
				return oldAnnotation.enableInstallPrompt();
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return oldAnnotation.annotationType();
			}
		};

		Method method = null;
		Field annotations = null;

		try {
			method = Class.class.getDeclaredMethod(ANNOTATION_DATA);
			method.setAccessible(true);

			Object annotationData = method.invoke(classWithPwaAnnotation);
			annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
			annotations.setAccessible(true);

			@SuppressWarnings("unchecked")
			Map<Class<? extends Annotation>, Annotation> map =
					(Map<Class<? extends Annotation>, Annotation>) annotations.get(annotationData);
			map.put(PWA.class, newPwa);

			PWA newAnnotation = classWithPwaAnnotation.getDeclaredAnnotation(PWA.class);
			LOGGER.info("new PWA: shortName = {}, name = {}", newAnnotation.shortName(), newAnnotation.name());
		} catch (Exception e) {
			throw new PwaModificationException(e);
		} finally {
			if (method != null) {
				method.setAccessible(false);
			}

			if (annotations != null) {
				annotations.setAccessible(false);
			}
		}
	}

	private static final class PwaModificationException extends RuntimeException {
		PwaModificationException(final Throwable cause) {
			super(cause);
		}
	}
}
