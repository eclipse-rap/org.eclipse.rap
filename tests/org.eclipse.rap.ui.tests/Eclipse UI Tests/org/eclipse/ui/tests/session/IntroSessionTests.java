/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
package org.eclipse.ui.tests.session;

import junit.framework.TestCase;

import org.eclipse.ui.PlatformUI;

/**
 * Tests intro-related session properties.
 *
 * @since 3.1
 */
public class IntroSessionTests extends TestCase {

	/**
	 * @param name
	 */
	public IntroSessionTests(String name) {
		super(name);
	}

	public void testIntro() {
		//assert that the intro was not shown
		assertNull(PlatformUI.getWorkbench().getIntroManager().getIntro());
	}
}
