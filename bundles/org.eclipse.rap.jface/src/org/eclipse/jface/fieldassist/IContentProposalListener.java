/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.jface.fieldassist;

import java.io.Serializable;

/**
 * This interface is used to listen to notifications from a
 * {@link ContentProposalAdapter}.
 * 
 * @since 1.0
 */
public interface IContentProposalListener extends Serializable {
	/**
	 * A content proposal has been accepted.
	 * 
	 * @param proposal
	 *            the accepted content proposal
	 */
	public void proposalAccepted(IContentProposal proposal);
}
