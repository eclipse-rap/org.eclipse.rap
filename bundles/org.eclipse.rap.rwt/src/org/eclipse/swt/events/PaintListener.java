/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
package org.eclipse.swt.events;


import org.eclipse.swt.internal.SWTEventListener;

/**
 * Classes which implement this interface provide methods
 * that deal with the events that are generated when the
 * control needs to be painted. 
 * <p>
 * After creating an instance of a class that implements
 * this interface it can be added to a control using the
 * <code>addPaintListener</code> method and removed using
 * the <code>removePaintListener</code> method. When a
 * paint event occurs, the paintControl method will be
 * invoked.
 * </p>
 *
 * @see PaintEvent
 * @since 1.3
 */
public interface PaintListener extends SWTEventListener {

/**
 * Sent when a paint event occurs for the control.
 *
 * @param event an event containing information about the paint
 */
public void paintControl(PaintEvent event);
}
