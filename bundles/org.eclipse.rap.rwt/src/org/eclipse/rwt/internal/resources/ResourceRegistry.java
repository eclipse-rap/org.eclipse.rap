/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.rwt.internal.util.ParamCheck;
import org.eclipse.rwt.resources.IResource;


public class ResourceRegistry {
  private final Set resources;

  public ResourceRegistry() {
    resources = new LinkedHashSet();
  }
  
  public void add( final IResource resource ) {
    ParamCheck.notNull( resource, "resource" );
    resources.add( resource );
  }
  
  public IResource[] get() {
    IResource[] result = new IResource[ resources.size() ];
    resources.toArray( result );
    return result;
  }
  
  public void clear() {
    resources.clear();
  }
}
