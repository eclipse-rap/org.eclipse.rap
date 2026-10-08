/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.forms;

import org.eclipse.ui.internal.forms.widgets.FormsResources;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class FormsPlugin extends AbstractUIPlugin {

	public FormsPlugin() {
	}
	
	public void stop(BundleContext context) throws Exception {
		try {
			FormsResources.shutdown();
		} finally {
			super.stop(context);
		}
	}

}
