/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
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
package org.eclipse.ui.tests.presentations;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for all areas of the presentations code for the platform.
 */
public final class PresentationsTestSuite extends TestSuite {

    /**
     * Returns the suite. This is required to use the JUnit Launcher.
     */
    public static final Test suite() {
        return new PresentationsTestSuite();
    }

    /**
     * Construct the test suite.
     */
    public PresentationsTestSuite() {
//        addTest(new TestSuite(Bug48589Test.class));
    }
}
