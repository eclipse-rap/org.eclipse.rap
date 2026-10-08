/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
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
package org.eclipse.ui.internal;

/**
 */
public class DirtyPerspectiveMarker {
	/**
	 * @param id
	 */
	public DirtyPerspectiveMarker(String id) {
		perspectiveId = id;
	}

	public String perspectiveId;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return perspectiveId.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o instanceof DirtyPerspectiveMarker) {
			return perspectiveId
					.equals(((DirtyPerspectiveMarker) o).perspectiveId);
		}
		return false;
	}
}
