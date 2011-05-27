/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;

public final class TestResourceManagerFactory implements IResourceManagerFactory {
  
  public IResourceManager create() {
    return new TestResourceManager();
  }
}