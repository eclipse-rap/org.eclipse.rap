/*******************************************************************************
 * Copyright (c) 2014, 2015 Raymond Augé and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Raymond Augé <raymond.auge@liferay.com> - Bug 436698
 ******************************************************************************/

package org.eclipse.rap.http.servlet.internal.util;

import org.eclipse.rap.http.servlet.internal.*;
import org.eclipse.rap.http.servlet.internal.servlet.ProxyServlet;
import org.eclipse.rap.service.servlet.internal.runtime.HttpServiceRuntime;
import org.osgi.framework.ServiceRegistration;

public class HttpTuple {

	public HttpTuple(ProxyServlet proxyServlet, HttpServiceFactory httpServiceFactory,
			ServiceRegistration<?> hsfRegistration, HttpServiceRuntimeImpl httpServiceRuntime,
			ServiceRegistration<HttpServiceRuntime> hsrRegistration) {

		this.proxyServlet = proxyServlet;
		this.httpServiceFactory = httpServiceFactory;
		this.hsfRegistration = hsfRegistration;
		this.httpServiceRuntime = httpServiceRuntime;
		this.hsrRegistration = hsrRegistration;
	}

	public void destroy() {
		Activator.unregisterHttpService(proxyServlet);
		proxyServlet.setHttpServiceRuntimeImpl(null);
		hsfRegistration.unregister();
		httpServiceRuntime.setHsrRegistration(null);
		hsrRegistration.unregister();
		httpServiceRuntime.destroy();
	}

	final HttpServiceFactory httpServiceFactory;
	final ServiceRegistration<?> hsfRegistration;
	final HttpServiceRuntimeImpl httpServiceRuntime;
	final ServiceRegistration<HttpServiceRuntime> hsrRegistration;
	final ProxyServlet proxyServlet;

}
