/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.events;

import java.util.function.Consumer;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Classes which implement this interface provide methods
 * that deal with the events that are generated as mouse buttons
 * are pressed.
 * <p>
 * After creating an instance of a class that implements
 * this interface it can be added to a control using the
 * <code>addMouseListener</code> method and removed using
 * the <code>removeMouseListener</code> method. When a
 * mouse button is pressed or released, the appropriate method
 * will be invoked.
 * </p>
 *
 * @see MouseAdapter
 * @see MouseEvent
 * 
 * @since 1.1
 */
public interface MouseListener extends SWTEventListener {

/**
 * Sent when a mouse button is pressed twice within the 
 * (operating system specified) double click period.
 *
 * @param e an event containing information about the mouse double click
 *
 * @see org.eclipse.swt.widgets.Display#getDoubleClickTime()
 */
public void mouseDoubleClick(MouseEvent e);

/**
 * Sent when a mouse button is pressed.
 *
 * @param e an event containing information about the mouse button press
 */
public void mouseDown(MouseEvent e);

/**
 * Sent when a mouse button is released.
 *
 * @param e an event containing information about the mouse button release
 */
public void mouseUp(MouseEvent e);

/**
 * Static helper method to create a <code>MouseListener</code> for the
 * {@link #mouseDoubleClick(MouseEvent e)}) method with a lambda expression.
 *
 * @param c the consumer of the event
 * @return MouseListener
 * @since 4.1
 */
static MouseListener mouseDoubleClickAdapter(Consumer<MouseEvent> c) {
    return new MouseAdapter() {
        @Override
        public void mouseDoubleClick(MouseEvent e) {
            c.accept(e);
        }
    };
}

/**
 * Static helper method to create a <code>MouseListener</code> for the
 * {@link #mouseDown(MouseEvent e)}) method with a lambda expression.
 *
 * @param c the consumer of the event
 * @return MouseListener
 * @since 4.1
 */
static MouseListener mouseDownAdapter(Consumer<MouseEvent> c) {
    return new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
            c.accept(e);
        }
    };
}

/**
 * Static helper method to create a <code>MouseListener</code> for the
 * {@link #mouseUp(MouseEvent e)}) method with a lambda expression.
 *
 * @param c the consumer of the event
 * @return MouseListener
 * @since 4.1
 */
static MouseListener mouseUpAdapter(Consumer<MouseEvent> c) {
    return new MouseAdapter() {
        @Override
        public void mouseUp(MouseEvent e) {
            c.accept(e);
        }
    };
}
}
