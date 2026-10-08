/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.ui.tests.operations;


/**
 * @since 3.1
 */
public class UnredoableTestOperation extends TestOperation {
	UnredoableTestOperation(String name) {
		super(name);
	}

	boolean disposed = false;
	
	public boolean canRedo() {
		return false;
	}
	
	public void dispose() {
		disposed = true;
	}

}
