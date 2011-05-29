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

package org.eclipse.rap.ui.tests;

import junit.framework.TestCase;

import org.eclipse.rap.ui.tests.resources.*;
import org.eclipse.rwt.internal.engine.RWTFactory;
import org.eclipse.rwt.resources.IResource;

public class RWTConfigurationWrapper extends TestCase {
  
  public void testRegisterResourcesWithDependencies() {
    IResource[] resources = RWTFactory.getResourceRegistry().get();
    int resource1Index = -1;
    int resource2Index = -1;
    int resource3Index = -1;
    int resource4Index = -1;
    int resource5Index = -1;
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ] instanceof Resource1 ) {
        resource1Index = i;
      } else if( resources[ i ] instanceof Resource2 ) {
        resource2Index = i;
      } else if( resources[ i ] instanceof Resource3 ) {
        resource3Index = i;
      } else if( resources[ i ] instanceof Resource4 ) {
        resource4Index = i;
      } else if( resources[ i ] instanceof Resource5 ) {
        resource5Index = i;
      }
    }
    // expected Resource4, Resource5, Resource3, Resource2, Resource1
    assertTrue( resource4Index < resource5Index );
    assertTrue( resource5Index < resource3Index );
    assertTrue( resource3Index < resource2Index );
    assertTrue( resource2Index < resource1Index );
  }
}