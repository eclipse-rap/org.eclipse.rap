/*******************************************************************************
 * Copyright (c) 2011 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import org.eclipse.rwt.internal.engine.RWTContext;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;

final class FakeResourceManagerRWTContext extends RWTContext {
  private final RWTContext rwtContext;
  private final TestResourceManager testResourceManager;

  FakeResourceManagerRWTContext( RWTContext rwtContext ) {
    this.rwtContext = rwtContext;
    this.testResourceManager = new TestResourceManager();
  }

  public Object getInstance( Class instanceType ) {
    Object result = rwtContext.getInstance( instanceType );
    if( instanceType == ResourceManagerImpl.class ) {
      result = testResourceManager;
    }
    return result;
  }
}