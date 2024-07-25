/*******************************************************************************
 * Copyright (c) 2016, 2020 Raymond Augé and others.
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
 *     Dirk Fauth <dirk.fauth@googlemail.com> - Bug 567831
 *******************************************************************************/
package org.eclipse.rap.http.servlet.internal.multipart;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import java.io.*;
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload2.core.*;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.eclipse.rap.service.servlet.internal.runtime.dto.ServletDTO;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class MultipartSupportImpl implements MultipartSupport {

	public MultipartSupportImpl(ServletDTO servletDTO, ServletContext servletContext) {
		this.servletDTO = servletDTO;

		// Must return non-null File. See Servlet 3.1 §4.8.1
		File baseStorage = (File) servletContext.getAttribute(ServletContext.TEMPDIR);

		if (servletDTO.multipartLocation.length() > 0) {
			File storage = new File(servletDTO.multipartLocation);

			if (!storage.isAbsolute()) {
				storage = new File(baseStorage, storage.getPath());
			}

			baseStorage = storage;
		}

		checkPermission(baseStorage, servletContext);

		baseStorage.mkdirs();

		DiskFileItemFactory factory = DiskFileItemFactory.builder()
			.setPath(baseStorage.toPath())
			.setBufferSize(servletDTO.multipartFileSizeThreshold)
			.get();

		upload = new JakartaServletFileUpload(factory);

		if (servletDTO.multipartMaxFileSize > -1L) {
			upload.setFileSizeMax(servletDTO.multipartMaxFileSize);
		}

		if (servletDTO.multipartMaxRequestSize > -1L) {
			upload.setSizeMax(servletDTO.multipartMaxRequestSize);
		}
	}

	private void checkPermission(File baseStorage, ServletContext servletContext) {
		BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext"); //$NON-NLS-1$
		Bundle bundle = bundleContext.getBundle();
		AccessControlContext accessControlContext = bundle.adapt(AccessControlContext.class);
		if (accessControlContext == null)
			return;
		accessControlContext.checkPermission(new FilePermission(baseStorage.getAbsolutePath(), "read,write")); //$NON-NLS-1$
	}

	@Override
	public List<Part> parseRequest(HttpServletRequest request) throws IOException, ServletException {
		if (upload == null) {
			throw new IllegalStateException("Servlet was not configured for multipart!"); //$NON-NLS-1$
		}

		if (!servletDTO.multipartEnabled) {
			throw new IllegalStateException("No multipart config on " + servletDTO); //$NON-NLS-1$
		}

		if (!JakartaServletFileUpload.isMultipartContent(request)) {
			throw new ServletException("Not a multipart request!"); //$NON-NLS-1$
		}

		ArrayList<Part> parts = new ArrayList<>();

		try {
			for (Object item : upload.parseRequest(request)) {
				DiskFileItem diskFileItem = (DiskFileItem) item;

				parts.add(new MultipartSupportPart(diskFileItem));
			}
		} catch (FileUploadException fnfe) {
			throw new IOException(fnfe);
		}

		return parts;
	}

	private final ServletDTO servletDTO;
	private final JakartaServletFileUpload upload;

}
