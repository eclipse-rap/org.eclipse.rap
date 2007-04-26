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

package org.eclipse.swt.resources;

import junit.framework.TestCase;
import org.eclipse.swt.RWTFixture;
import org.eclipse.swt.RWTFixture.TestResourceManager;
import com.w4t.IResourceManager;


public class ResourceManager_Test extends TestCase {
  
  public void testResourceManagerFactoryRegistration() {
    ResourceManager.register( new IResourceManagerFactory() {
      public IResourceManager create() {
        return new TestResourceManager();
      }
    } );
    IResourceManager manager1 = ResourceManager.getInstance();
    assertTrue( manager1 instanceof TestResourceManager );
    IResourceManager manager2 = ResourceManager.getInstance();
    assertSame( manager1, manager2 );

    try {
      ResourceManager.register( new IResourceManagerFactory() {
        public IResourceManager create() {
          return null;
        }
      } );
      fail( "Only one factory at a time could be registered." );
    } catch( final IllegalStateException ise ) {
      // expected
    }
    // cleanup for subsequent tests
    RWTFixture.deregisterResourceManager();
  }
}
