/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
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
package org.eclipse.jface.tests.dialogs;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new AllTests();
    }

    public AllTests() {
    	addTestSuite(DialogTest.class);
    	addTestSuite(DialogSettingsTest.class);
    	addTestSuite(InputDialogTest.class);
        addTestSuite(TitleAreaDialogTest.class);
        addTestSuite(SafeRunnableErrorTest.class);
        addTestSuite(ProgressIndicatorStyleTest.class);
    	addTestSuite(ProgressMonitorDialogTest.class);
    }
}
