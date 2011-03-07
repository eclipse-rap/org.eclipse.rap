/*******************************************************************************
 * Copyright (c) 2006, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.resources.JsConcatenator;
import org.eclipse.rwt.internal.resources.ResourceManagerImpl;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.resources.IResourceManagerFactory;


final class ResourceManagerFactory implements IResourceManagerFactory {

  public IResourceManager create() {
    IResourceManager result = ResourceManagerImpl.getInstance();
    Adaptable adaptable = ( Adaptable )result;
    JsConcatenator jsConcatenator = ( JsConcatenator )adaptable.getAdapter( JsConcatenator.class );
    jsConcatenator.startJsConcatenation();
    return result;
  }
}
