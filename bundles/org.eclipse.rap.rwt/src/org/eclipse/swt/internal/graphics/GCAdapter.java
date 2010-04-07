/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.LinkedList;
import java.util.List;


public final class GCAdapter implements IGCAdapter {

  private final List gcOperations;
  
  public GCAdapter() {
    gcOperations = new LinkedList();
  }
  
  public void addGCOperation( final GCOperation operation ) {
    gcOperations.add( operation );
  }
  
  public GCOperation[] getGCOperations() {
    GCOperation[] result = new GCOperation[ gcOperations.size() ];
    gcOperations.toArray( result );
    return result;
  }
  
  public void clearGCOperations() {
    gcOperations.clear();
  }
}