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

package org.eclipse.rwt.internal.engine;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.AdapterManagerImpl;


public final class TestAdaptable implements Adaptable {
  public Object getAdapter( final Class adapter ) {
    AdapterManager adapterManager = AdapterManagerImpl.getInstance();
    return adapterManager.getAdapter( this, adapter );
  }
}