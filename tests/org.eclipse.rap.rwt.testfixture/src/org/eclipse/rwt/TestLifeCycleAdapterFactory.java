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

import org.eclipse.rwt.internal.lifecycle.LifeCycleAdapterFactory;


public class TestLifeCycleAdapterFactory implements AdapterFactory {

  private static AdapterFactory factory = new LifeCycleAdapterFactory();
  
  public Object getAdapter( Object adaptable, Class adapter ) {
    return factory.getAdapter( adaptable, adapter );
  }

  public Class[] getAdapterList() {
    return factory.getAdapterList();
  }
}
