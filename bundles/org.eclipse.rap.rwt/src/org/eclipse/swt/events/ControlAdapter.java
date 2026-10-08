/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

/**
 * This adapter class provides default implementations for the
 * methods described by the <code>ControlListener</code> interface.
 * <p>
 * Classes that wish to deal with <code>ControlEvent</code>s can
 * extend this class and override only the methods which they are
 * interested in.
 * </p>
 *
 * @see ControlListener
 * @see ControlEvent
 */
public abstract class ControlAdapter implements ControlListener {

/**
 * Sent when the location (x, y) of a control changes relative
 * to its parent (or relative to the display, for <code>Shell</code>s).
 * The default behavior is to do nothing.
 *
 * @param e an event containing information about the move
 */
public void controlMoved(ControlEvent e) {
}

/**
 * Sent when the size (width, height) of a control changes.
 * The default behavior is to do nothing.
 *
 * @param e an event containing information about the resize
 */
public void controlResized(ControlEvent e) {
}
}

