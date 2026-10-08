/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.tests.statushandlers;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the status handling facility
 *
 * @since 3.3
 */
public class StatusHandlingTestSuite extends TestSuite {

	public StatusHandlingTestSuite() {
//		addTest(new TestSuite(WizardsStatusHandlingTestCase.class));
		addTest(new TestSuite(StatusDialogManagerTest.class));
		addTest(new TestSuite(LabelProviderWrapperTest.class));
		addTest(new TestSuite(SupportTrayTest.class));
		addTest(new TestSuite(WorkbenchStatusDialogManagerImplTest.class));
	}

	public static Test suite() {
		return new StatusHandlingTestSuite();
	}
}
