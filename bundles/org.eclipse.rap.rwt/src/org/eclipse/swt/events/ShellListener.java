/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.events;

import java.util.function.Consumer;

import org.eclipse.swt.internal.SWTEventListener;

/**
 * Classes which implement this interface provide methods
 * that deal with changes in state of <code>Shell</code>s.
 * <p>
 * After creating an instance of a class that implements
 * this interface it can be added to a shell using the
 * <code>addShellListener</code> method and removed using
 * the <code>removeShellListener</code> method. When the
 * state of the shell changes, the appropriate method will
 * be invoked.
 * </p>
 *
 * @see ShellAdapter
 * @see ShellEvent
 */
public interface ShellListener extends SWTEventListener {

/**
 * Sent when a shell becomes the active window.
 *
 * @param e an event containing information about the activation
 */
public void shellActivated(ShellEvent e);

/**
 * Sent when a shell is closed.
 *
 * @param e an event containing information about the close
 */
public void shellClosed(ShellEvent e);

/**
 * Sent when a shell stops being the active window.
 *
 * @param e an event containing information about the deactivation
 */
public void shellDeactivated(ShellEvent e);

/**
 * Sent when a shell is un-minimized.
 *
 * @param e an event containing information about the un-minimization
 */
//public void shellDeiconified(ShellEvent e);

/**
 * Sent when a shell is minimized.
 *
 * @param e an event containing information about the minimization
 */
//public void shellIconified(ShellEvent e);

/**
 * Static helper method to create a <code>ShellListener</code> for the
 * {@link #shellActivated(ShellEvent e)}) method, given a lambda expression or a method reference.
 *
 * @param c the consumer of the event
 * @return ShellListener
 * @since 4.1
 */
static ShellListener shellActivatedAdapter(Consumer<ShellEvent> c) {
    return new ShellAdapter() {
        @Override
        public void shellActivated(ShellEvent e) {
            c.accept(e);
        }
    };
}

/**
 * Static helper method to create a <code>ShellListener</code> for the
 * {@link #shellClosed(ShellEvent e)}) method, given a lambda expression or a method reference.
 *
 * @param c the consumer of the event
 * @return ShellListener
 * @since 4.1
 */
static ShellListener shellClosedAdapter(Consumer<ShellEvent> c) {
    return new ShellAdapter() {
        @Override
        public void shellClosed(ShellEvent e) {
            c.accept(e);
        }
    };
}

/**
 * Static helper method to create a <code>ShellListener</code> for the
 * {@link #shellDeactivated(ShellEvent e)}) method, given a lambda expression or a method reference.
 *
 * @param c the consumer of the event
 * @return ShellListener
 * @since 4.1
 */
static ShellListener shellDeactivatedAdapter(Consumer<ShellEvent> c) {
    return new ShellAdapter() {
        @Override
        public void shellDeactivated(ShellEvent e) {
            c.accept(e);
        }
    };
}
}

