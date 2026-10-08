/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
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
package org.eclipse.ui.tests.preferences;

//import org.eclipse.ui.tests.propertyPages.PropertyPageEnablementTest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite for preferences.
 */
public class PreferencesTestSuite extends TestSuite {

	/**
	 * Returns the suite. This is required to use the JUnit Launcher.
	 */
	public static Test suite() {
		return new PreferencesTestSuite();
	}

	/**
	 * Construct the test suite.
	 */
	public PreferencesTestSuite() {
		addTest(new TestSuite(FontPreferenceTestCase.class));
		addTest(new TestSuite(DeprecatedFontPreferenceTestCase.class));
		addTest(new TestSuite(ScopedPreferenceStoreTestCase.class));
		addTest(new TestSuite(WorkingCopyPreferencesTestCase.class));
// All test in PropertyPageEnablementTest failed with ResourceException:
// Resource '/TestProject' already exist.
//		addTest(new TestSuite(PropertyPageEnablementTest.class));
		addTest(new TestSuite(ListenerRemovalTestCase.class));
	}
}
