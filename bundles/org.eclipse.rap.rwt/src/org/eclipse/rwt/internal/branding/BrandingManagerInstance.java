/*******************************************************************************
 * Copyright (c) 2007, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal.branding;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.internal.util.ParamCheck;


public class BrandingManagerInstance {
  private final List brandings;
  
  private BrandingManagerInstance() {
    brandings = new LinkedList();
  }

  void register( final AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.add( branding );
    }
  }
  
  void deregister( final AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.remove( branding );
    }
  }
  
  AbstractBranding[] getAll() {
    AbstractBranding[] result;
    synchronized( brandings ) {
      result = new AbstractBranding[ brandings.size() ];
      brandings.toArray( result );
    }
    return result;
  }
}