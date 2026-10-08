/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
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
package org.eclipse.jface.tests.labelProviders;

import junit.framework.Test;
import junit.framework.TestSuite;

public class DecoratingLabelProviderTests extends TestSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new DecoratingLabelProviderTests();
    }

    public DecoratingLabelProviderTests() {
    	addTestSuite(CompositeLabelProviderTableTest.class);
    	addTestSuite(DecoratingLabelProviderTreePathTest.class);
        addTestSuite(DecoratingLabelProviderTreeTest.class);
        addTestSuite(ColorAndFontLabelProviderTest.class);
        addTestSuite(ColorAndFontViewerLabelProviderTest.class);
//        addTestSuite(DecoratingStyledCellLabelProviderTest.class);
//        addTestSuite(IDecorationContextTest.class);
    }
}
