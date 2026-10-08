/*******************************************************************************
 * Copyright (c) 2009, 2013 IBM Corporation and others.
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
package org.eclipse.e4.ui.internal.workbench;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.e4.core.contexts.ContextFunction;
import org.eclipse.e4.core.contexts.IEclipseContext;

/**
 * This class provides a context function that returns a default progress monitor. This is generally
 * used near the root of a context tree to provide a reasonable default monitor for cases where more
 * specific contexts have not provided one.
 */
public class ProgressMonitorFunction extends ContextFunction {

	@Override
	public Object compute(IEclipseContext context, String contextKey) {
		return new NullProgressMonitor();
	}

}
