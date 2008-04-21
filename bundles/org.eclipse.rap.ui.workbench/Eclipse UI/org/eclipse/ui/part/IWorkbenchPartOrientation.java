/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.part;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;

// RAP [rh] This interface was already available in 1.0, changed JavaDoc to
//     clarify its limited usefulness as RWT does not have BiDi support

/**
 * The IWorkbenchPartOrientation is the interface that defines the orientation
 * of the part. If a type does not implement this interface an orientation of
 * SWT.NONE will be assumed.
 * 
 * <hr />
 * <strong>RAP Specific Note:</strong> Implementing this interface is useless 
 * as RWT does not have bidi-support yet. Therefore the constants 
 * SWT.LEFT_TO_RIGHT and SWT.RIGHT_TO_LEFT aren't availble either. 
 * 
 * <!-- 
 * @see org.eclipse.swt.SWT#RIGHT_TO_LEFT
 * @see org.eclipse.swt.SWT#LEFT_TO_RIGHT 
 * -->
 * @see org.eclipse.swt.SWT#NONE
 * @see Window#getDefaultOrientation()
 * @since 1.0
 */
public interface IWorkbenchPartOrientation {
	/**
	 * Return the orientation of this part.
	 * 
	 * <!-- @return int SWT#RIGHT_TO_LEFT or SWT#LEFT_TO_RIGHT -->
	 * @return int SWT.NONE
	 * @see Window#getDefaultOrientation()
	 * <!--
	 * @see SWT#RIGHT_TO_LEFT
	 * @see SWT#LEFT_TO_RIGHT
	 * -->
	 * @see Window#getDefaultOrientation()
	 */
	public int getOrientation();
}
