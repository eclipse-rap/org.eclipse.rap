/*******************************************************************************
 * Copyright (c) 2014, 2019 Raymond Augé and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Raymond Augé - bug fixes and enhancements
 ******************************************************************************/

package org.eclipse.rap.http.servlet.internal.util;

/**
 * @author Raymond Augé
 */
public class Const {

	public static final String AMP = "&"; //$NON-NLS-1$
	public static final String BLANK = ""; //$NON-NLS-1$
	public static final String CLOSE_PAREN = ")"; //$NON-NLS-1$
	public static final String CONTEXT_PATH = "context.path"; //$NON-NLS-1$
	public static final String DOT = "."; //$NON-NLS-1$
	public static final String[] EMPTY_ARRAY = new String[0];
	public static final String EQUAL = "="; //$NON-NLS-1$
	public static final String FILTER_NAME = "filter-name"; //$NON-NLS-1$
	public static final String FILTER_PRIORITY = "filter-priority"; //$NON-NLS-1$
	public static final String HTTP = "http"; //$NON-NLS-1$
	public static final String HTTP_HOST = "http.host"; //$NON-NLS-1$
	public static final String HTTP_PORT = "http.port"; //$NON-NLS-1$
	public static final String HTTPS = "https"; //$NON-NLS-1$
	public static final String HTTPS_HOST = "https.host"; //$NON-NLS-1$
	public static final String HTTPS_PORT = "https.port"; //$NON-NLS-1$
	public static final String LOCALHOST = "localhost"; //$NON-NLS-1$
	public static final String OPEN_PAREN = "("; //$NON-NLS-1$
	public static final String PROTOCOL = "://"; //$NON-NLS-1$
	public static final String SERVLET_NAME = "servlet-name"; //$NON-NLS-1$
	public static final String SLASH = "/"; //$NON-NLS-1$
	public static final String SLASH_STAR = "/*"; //$NON-NLS-1$
	public static final String SLASH_STAR_DOT = "/*."; //$NON-NLS-1$
	public static final String STAR_DOT = "*."; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_MULTIPART_ENABLED = "equinox.http.multipartSupported"; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_MULTIPART_FILESIZETHRESHOLD = "equinox.http.whiteboard.servlet.multipart.fileSizeThreshold"; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_MULTIPART_LOCATION = "equinox.http.whiteboard.servlet.multipart.location"; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_MULTIPART_MAXFILESIZE = "equinox.http.whiteboard.servlet.multipart.maxFileSize"; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_MULTIPART_MAXREQUESTSIZE = "equinox.http.whiteboard.servlet.multipart.maxRequestSize"; //$NON-NLS-1$
	public static final String EQUINOX_LEGACY_TCCL_PROP = "equinox.legacy.tccl"; //$NON-NLS-1$
	public static final String EQUINOX_LEGACY_CONTEXT_SELECT = "equinox.context.select"; //$NON-NLS-1$
	public static final String EQUINOX_LEGACY_CONTEXT_HELPER = "equinox.legacy.context.helper"; //$NON-NLS-1$
	public static final String EQUINOX_LEGACY_HTTP_CONTEXT_INITIATING_ID = "equinox.legacy.http.context.initiating.id"; //$NON-NLS-1$
	public static final String EQUINOX_HTTP_WHITEBOARD_CONTEXT_HELPER_DEFAULT = "equinox.http.whiteboard.context.helper.default"; //$NON-NLS-1$
	/**
	 * If a servlet filter, error page or listener wants to be registered with
	 * the Http Context(s) managed by the Http Service, they can select the
	 * contexts having this property.
	 * <p>
	 * Servlets or resources registered using this property are treated as an
	 * invalid registration.
	 * 
	 * @see #HTTP_SERVICE_CONTEXT_FILTER
	 * @since 1.1
	 */
	public static final String HTTP_SERVICE_CONTEXT_PROPERTY = "osgi.http.whiteboard.context.httpservice"; //$NON-NLS-1$
	/**
	 * Http Runtime Service service property to associate the Http Runtime
	 * Service with one or more HttpService services.
	 * 
	 * <p>
	 * If this Http Whiteboard implementation also implements the Http Service
	 * Specification, this service property is set to a collection of
	 * {@code service.id} for the {@code HttpService} services registered by
	 * this implementation.
	 * 
	 * <p>
	 * The value of this service property must be of type
	 * {@code Collection<Long>}.
	 */
	public static final String HTTP_SERVICE_ID = "osgi.http.service.id"; //$NON-NLS-1$
}
