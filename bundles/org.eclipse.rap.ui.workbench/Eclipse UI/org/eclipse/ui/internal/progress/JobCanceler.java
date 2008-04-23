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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

// RAP [fappel]: This is a helper class used to avoid a memory leak due to 
//               thread management.
//               Note that this is still under investigation.
//               See comment in JobManagerAdapter
final class JobCanceler implements IJobChangeListener {

  public void aboutToRun( IJobChangeEvent event ) {
    event.getJob().cancel();
  }

  public void awake( IJobChangeEvent event ) {
    event.getJob().cancel();
  }

  public void done( IJobChangeEvent event ) {
    event.getJob().cancel();
  }

  public void running( IJobChangeEvent event ) {
    event.getJob().cancel();
  }

  public void scheduled( IJobChangeEvent event ) {
    event.getJob().cancel();
  }

  public void sleeping( IJobChangeEvent event ) {
    event.getJob().cancel();
  }
}