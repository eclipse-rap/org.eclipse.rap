/*******************************************************************************
 * Copyright (c) 2011, 2013 EclipseSource and others.
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

import org.eclipse.rap.rwt.internal.resources.ResourceRegistry.ResourceRegistration;
import org.eclipse.rap.rwt.internal.service.ContextProvider;


public class RWTConfigurationWrapper extends TestCase {

  private static final String RESOURCE1 = "org/eclipse/rap/ui/tests/resources/Resource1.js";
  private static final String RESOURCE2 = "org/eclipse/rap/ui/tests/resources/Resource2.js";
  private static final String RESOURCE3 = "org/eclipse/rap/ui/tests/resources/Resource3.js";
  private static final String RESOURCE4 = "org/eclipse/rap/ui/tests/resources/Resource4.js";
  private static final String RESOURCE5 = "org/eclipse/rap/ui/tests/resources/Resource5.js";

  public void testRegisterResourcesWithDependencies() {
    ResourceRegistration[] resources
      = ContextProvider.getApplicationContext().getResourceRegistry().getResourceRegistrations();
    int resource1Index = -1;
    int resource2Index = -1;
    int resource3Index = -1;
    int resource4Index = -1;
    int resource5Index = -1;
    for( int i = 0; i < resources.length; i++ ) {
      if( resources[ i ].getResourceName().equals( RESOURCE1 ) ) {
        resource1Index = i;
      } else if( resources[ i ].getResourceName().equals( RESOURCE2 ) ) {
        resource2Index = i;
      } else if( resources[ i ].getResourceName().equals( RESOURCE3 ) ) {
        resource3Index = i;
      } else if( resources[ i ].getResourceName().equals( RESOURCE4 ) ) {
        resource4Index = i;
      } else if( resources[ i ].getResourceName().equals( RESOURCE5 ) ) {
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