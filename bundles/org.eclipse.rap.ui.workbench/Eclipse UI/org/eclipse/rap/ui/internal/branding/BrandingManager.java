/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.branding;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.rap.rwt.internal.util.ParamCheck;


public class BrandingManager {
  private static BrandingManager instance;

  public static synchronized BrandingManager getInstance() {
    if( instance == null ) {
      instance = new BrandingManager();
    }
    return instance;
  }
  
  private final List<AbstractBranding> brandings;
  
  public BrandingManager() {
    brandings = new LinkedList<AbstractBranding>();
  }

  public void register( AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.add( branding );
    }
  }
  
  public void deregister( AbstractBranding branding ) {
    ParamCheck.notNull( branding, "branding" );
    synchronized( brandings ) {
      brandings.remove( branding );
    }
  }
  
  public void deregisterAll() {
    synchronized( brandings ) {
      brandings.clear();
    }
  }
  
  public AbstractBranding[] getAll() {
    synchronized( brandings ) {
      return brandings.toArray( new AbstractBranding[ brandings.size() ] );
    }
  }

}