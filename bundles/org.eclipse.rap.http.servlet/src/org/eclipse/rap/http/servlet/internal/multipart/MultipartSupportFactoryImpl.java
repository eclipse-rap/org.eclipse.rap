/*******************************************************************************
 * Copyright (c) 2016, 2019 Raymond Augé and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 497271
 *******************************************************************************/
package org.eclipse.rap.http.servlet.internal.multipart;

import jakarta.servlet.ServletContext;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.eclipse.rap.service.servlet.internal.runtime.dto.ServletDTO;

public class MultipartSupportFactoryImpl implements MultipartSupportFactory {

	public static final Class<?> FAIL_EARLY = FileUploadException.class;

	@Override
	public MultipartSupport newInstance(ServletDTO servletDTO, ServletContext servletContext) {
		return new MultipartSupportImpl(servletDTO, servletContext);
	}

}
