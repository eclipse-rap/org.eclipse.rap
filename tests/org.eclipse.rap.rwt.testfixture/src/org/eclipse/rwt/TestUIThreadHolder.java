/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import org.eclipse.rwt.internal.lifecycle.IUIThreadHolder;
import org.eclipse.rwt.internal.service.ServiceContext;

public class TestUIThreadHolder implements IUIThreadHolder {
  
  private final Thread thread;

  public TestUIThreadHolder( Thread thread ) {
    this.thread = thread;
  }
  
  public void updateServiceContext() {
  }

  public void terminateThread() {
  }

  public void switchThread() {
  }

  public void setServiceContext( ServiceContext serviceContext ) {
  }

  public Thread getThread() {
    return thread;
  }

  public Object getLock() {
    return null;
  }
}