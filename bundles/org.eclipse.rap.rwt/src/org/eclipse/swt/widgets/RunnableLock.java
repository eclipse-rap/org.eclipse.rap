/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
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
package org.eclipse.swt.widgets;

import org.eclipse.swt.internal.SerializableCompatibility;


/**
 * Instances of this class are used to ensure that an
 * application cannot interfere with the locking mechanism
 * used to implement asynchronous and synchronous communication
 * between widgets and background threads.
 */

class RunnableLock implements SerializableCompatibility {
	
  Runnable runnable;
	transient Thread thread;
	Throwable throwable;
	
RunnableLock (Runnable runnable) {
	this.runnable = runnable;
}

boolean done () {
	return runnable == null || throwable != null;
}

void run () {
	if (runnable != null) runnable.run ();
	runnable = null;
}

}
