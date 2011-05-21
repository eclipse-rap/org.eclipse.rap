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
package org.eclipse.rwt.internal.engine;

import junit.framework.TestCase;

import org.eclipse.rwt.TestServletContext;
import org.eclipse.rwt.internal.engine.RWTServletContextListener.Configurables;


public class Configurables_Test extends TestCase {
  private TestServletContext servletContext;
  private Configurables configurables;

  public void testBufferingOfConfigurables() {
    RWTServletContextListener.bufferConfigurables( configurables, servletContext );
    Configurables found = RWTServletContextListener.getConfigurables( servletContext );
    
    assertNotNull( found );
    assertSame( configurables, found );
  }
  
  public void testRemoveConfigurables() {
    RWTServletContextListener.bufferConfigurables( configurables, servletContext );

    RWTServletContextListener.removeConfigurables( servletContext );
    
    assertNull( RWTServletContextListener.getConfigurables( servletContext ) );
  }
  
  public void testConfigurablesAdd() {
    final boolean[] addConfigurableHasBeenCalled = new boolean[ 1 ];
    ApplicationContext applicationContext = new ApplicationContext() {
      public void addConfigurable( Configurable configurable ) {
        addConfigurableHasBeenCalled[ 0 ] = true;
      }
    };

    configurables.add( applicationContext );
    
    assertTrue( addConfigurableHasBeenCalled[ 0 ] );
  }

  public void testConfigurablesRemove() {
    final boolean[] removeConfigurableHasBeenCalled = new boolean[ 1 ];
    ApplicationContext applicationContext = new ApplicationContext() {
      public void removeConfigurable( Configurable configurable ) {
        removeConfigurableHasBeenCalled[ 0 ] = true;
      }
    };
    
    configurables.remove( applicationContext );
    
    assertTrue( removeConfigurableHasBeenCalled[ 0 ] );
  }
  
  protected void setUp() throws Exception {
    servletContext = new TestServletContext();
    configurables = new Configurables( servletContext );
  }
}