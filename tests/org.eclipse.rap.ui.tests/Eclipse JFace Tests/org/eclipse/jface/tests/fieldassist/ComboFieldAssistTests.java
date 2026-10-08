/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
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

package org.eclipse.jface.tests.fieldassist;

/**
 * @since 3.6
 *
 */
public class ComboFieldAssistTests extends FieldAssistTestCase {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.tests.fieldassist.AbstractFieldAssistTestCase#createFieldAssistWindow()
	 */
	protected AbstractFieldAssistWindow createFieldAssistWindow() {
		return new ComboFieldAssistWindow();
	}

}
