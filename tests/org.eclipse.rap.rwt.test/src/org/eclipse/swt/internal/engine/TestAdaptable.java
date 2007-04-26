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

package org.eclipse.swt.internal.engine;

import com.w4t.*;

public final class TestAdaptable implements Adaptable {
  public Object getAdapter( final Class adapter ) {
    AdapterManager adapterManager = W4TContext.getAdapterManager();
    return adapterManager.getAdapter( this, adapter );
  }
}