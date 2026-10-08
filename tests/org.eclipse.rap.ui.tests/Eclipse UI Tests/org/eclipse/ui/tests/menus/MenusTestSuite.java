/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
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
package org.eclipse.ui.tests.menus;


import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for all code related to menus. This includes the
 * <code>popupMenus</code> extension point, and others.
 */
public class MenusTestSuite extends TestSuite {

    /**
     * Returns the suite. This is required to use the JUnit Launcher.
     */
    public static Test suite() {
        return new MenusTestSuite();
    }

    /**
     * Construct the test suite.
     */
    public MenusTestSuite() {
//        addTest(new TestSuite(ObjectContributionTest.class));
        addTest(new TestSuite(MenuVisibilityTest.class));
        addTest(new TestSuite(MenuBaseTests.class));
        addTest(new TestSuite(MenuPopulationTest.class));
        addTest(new TestSuite(DynamicMenuTest.class));
//        addTest(new TestSuite(Bug231304Test.class));
        addTest(new TestSuite(ShowViewMenuTest.class));
        addTest(new TestSuite(Bug264804Test.class));
    }
}
