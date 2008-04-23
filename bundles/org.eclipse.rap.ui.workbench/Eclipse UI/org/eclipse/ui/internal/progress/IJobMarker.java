/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.progress;

// RAP [fappel]: This is a helper class used to avoid a memory leak due to 
//               thread management.
//               Note that this is still under investigation.
//               See comment in JobManagerAdapter
public interface IJobMarker {
  boolean canBeRemoved();
}
