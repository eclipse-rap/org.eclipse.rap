/*******************************************************************************
 * Copyright (c) Jan. 27, 2024 Liferay, Inc.
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

import java.net.URL;
import org.eclipse.rap.http.servlet.internal.util.Const;
import org.eclipse.rap.service.http.HttpContext;
import org.eclipse.rap.service.servlet.internal.context.ServletContextHelper;
import org.osgi.framework.Bundle;

public class DefaultServletContextHelper extends ServletContextHelper implements HttpContext {
	private final Bundle bundle;

	public DefaultServletContextHelper(Bundle bundle) {
		super(bundle);
		this.bundle = bundle;
	}

	@Override
	public URL getResource(String name) {
		if (name != null) {
			if (name.startsWith(Const.SLASH)) {
				name = name.substring(1);
			}

			return bundle.getResource(name);
		}
		return null;
	}

}
