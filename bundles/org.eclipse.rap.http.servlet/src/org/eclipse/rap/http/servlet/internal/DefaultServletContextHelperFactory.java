/*******************************************************************************
 * Copyright (c) Jan. 27, 2019 Liferay, Inc.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liferay, Inc. - initial API and implementation and/or initial
 *                    documentation
 ******************************************************************************/

package org.eclipse.rap.http.servlet.internal;

import org.eclipse.rap.service.servlet.internal.context.ServletContextHelper;
import org.osgi.framework.*;

public class DefaultServletContextHelperFactory implements ServiceFactory<ServletContextHelper> {
	@Override
	public ServletContextHelper getService(Bundle bundle, ServiceRegistration<ServletContextHelper> registration) {
		return new DefaultServletContextHelper(bundle);
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration<ServletContextHelper> registration,
			ServletContextHelper service) {
		// do nothing
	}
}
