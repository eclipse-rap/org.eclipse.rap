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
package org.eclipse.ui.tests.themes;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @since 3.0
 */
public class ThemesTestSuite extends TestSuite {

    public static Test suite() {
        return new ThemesTestSuite();
    }

    public ThemesTestSuite() {
        addTest(new TestSuite(ThemeAPITest.class));
        addTest(new TestSuite(JFaceThemeTest.class));
    }
}
