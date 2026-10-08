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
package org.eclipse.ui.tests.performance;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.ui.tests.performance.presentations.PresentationPerformanceTestSuite;

/**
 * Test all areas of the UI API.
 */
public class UIPerformanceTestSuite extends TestSuite {

    /**
     * Returns the suite. This is required to use the JUnit Launcher.
     */
    public static Test suite() {
    	return new UIPerformanceTestSetup(new UIPerformanceTestSuite());
    }

    /**
     * Construct the test suite.
     */
    public UIPerformanceTestSuite() {
        addTest(new ActivitiesPerformanceSuite());
        addTest(new PresentationPerformanceTestSuite());
        addTest(new WorkbenchPerformanceSuite());
        addTest(new ViewPerformanceSuite());
        addTest(new EditorPerformanceSuite());
//        addTest(new TestSuite(CommandsPerformanceTest.class));
    }
}
