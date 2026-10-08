/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
package org.eclipse.ui.internal;

/**
 * Internal interface used in providing increased binary compatibility for
 * pre-3.0 plug-ins. There is an alternate declaration of this interface
 * elsewhere that declares some methods that existed on IWorkbenchPage
 * in 2.1 that were removed in 3.0 (because they referenced resource API).
 * 
 */
public interface ICompatibleWorkbenchPage {
    // dummy version that declares no methods
}
