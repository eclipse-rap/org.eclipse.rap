/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.util;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.rwt.SessionSingletonBase;


public class SessionSingletonEventManager extends SessionSingletonBase {

	/*
	 * As Workbench needs to inherit from EventManager AND SessionSingleton
	 * we introduced a new layer in between to fake the methods.
	 * As EventManager is abstract and has final methods, we use methodE()
	 * to delegate to the original 
	 */
	private final class EventManagerExtension extends EventManager {
		public synchronized final void addListenerObjectE(final Object listener) {
			addListenerObject( listener );
		}

		public synchronized final void clearListenersE() {
			clearListeners();
		}

		public final Object[] getListenersE() {
			return getListeners();
		}

		public final boolean isListenerAttachedE() {
			return isListenerAttached();
		}

		public synchronized final void removeListenerObjectE(final Object listener) {
			removeListenerObject( listener );
		}
	}

	private EventManagerExtension manager;
	
	public SessionSingletonEventManager() {

		manager = new EventManagerExtension();
	}
	
	protected synchronized final void addListenerObject(final Object listener) {
		manager.addListenerObjectE( listener );
	}
	
	protected synchronized final void clearListeners() {
		manager.clearListenersE();
	}
	
	protected final Object[] getListeners() {
		return manager.getListenersE();
	}
	
	protected final boolean isListenerAttached() {
		return manager.isListenerAttachedE();
	}
	
	protected synchronized final void removeListenerObject(final Object listener) {
		manager.removeListenerObjectE( listener );
	}
	
}
