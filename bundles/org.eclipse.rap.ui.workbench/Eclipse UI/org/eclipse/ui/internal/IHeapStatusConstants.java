/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
 * Preference constants for the heap status.
 * 
 */
public interface IHeapStatusConstants {

	/**
	 * Preference key for the update interval (value in milliseconds).
	 */
    String PREF_UPDATE_INTERVAL = "HeapStatus.updateInterval"; //$NON-NLS-1$

    /**
     * Preference key for whether to show max heap, if available (value is boolean).
     */
    String PREF_SHOW_MAX = "HeapStatus.showMax";   //$NON-NLS-1$
    	
//    /**
//     * ID for the Kyrsoft Memory Monitor plug-in.
//     */
//    String KYRSOFT_PLUGIN_ID = "de.kyrsoft.memmonitor"; //$NON-NLS-1$
//
//    /**
//     * ID for the Kyrsoft Memory Monitor view.
//     */
//    String KYRSOFT_VIEW_ID = "de.kyrsoft.memmonitor.views.MemoryView"; //$NON-NLS-1$
//

}
