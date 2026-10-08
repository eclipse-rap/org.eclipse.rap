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
package org.eclipse.ui.internal.intro;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.intro.IIntroPart;

/**
 * Describes an introduction extension.
 */
public interface IIntroDescriptor {

    /**
     * Creates an instance of the intro part defined in the descriptor.
     */
    IIntroPart createIntro() throws CoreException;

    /**
     * Returns the part id.
     *
     * @return the id of the part
     */
    public String getId();

    /**
     * Returns the descriptor of the image for this part.
     *
     * @return the descriptor of the image to display next to this part
     */
    public ImageDescriptor getImageDescriptor();
    
    /**
	 * Return the label override string for this part.
	 * 
	 * @return the label override string or <code>null</code> if one has not
	 *         been specified
	 */
	public String getLabelOverride();
}
