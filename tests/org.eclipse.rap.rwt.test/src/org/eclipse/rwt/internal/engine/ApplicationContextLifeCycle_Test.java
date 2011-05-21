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


public class ApplicationContextLifeCycle_Test extends TestCase {
  private ApplicationContext context;
  
  
  private static class TestConfigurable implements Configurable {
    private ApplicationContext configureContext;
    private ApplicationContext resetContext;
    private boolean activatedOnConfigure;
    private boolean activatedOnReset;

    public void configure( ApplicationContext context ) {
      this.configureContext = context;
      activatedOnConfigure = context.isActivated();
    }

    public void reset( ApplicationContext context ) {
      this.resetContext = context;
      activatedOnReset = context.isActivated();
    }

    ApplicationContext getConfigureContext() {
      return configureContext;
    }
    
    ApplicationContext getResetContext() {
      return resetContext;
    }

    boolean isActivatedOnConfigure() {
      return activatedOnConfigure;
    }

    boolean isActivatedOnReset() {
      return activatedOnReset;
    }
  }
  
  private static class TestActivatable implements Activatable {
    private boolean activated;
    private boolean deactivated;
    
    public void activate() {
      activated = true;
    }
    
    public void deactivate() {
      deactivated = true;
    }

    boolean activated() {
      return activated;
    }

    public boolean deactivated() {
      return deactivated;
    }
  }

  
  private static class TestActivationOrderConfigurable implements Configurable {
    private boolean activatedDuringConfigure;
    private boolean deactivatedDuringReset;
    private TestActivatable activatable;

    public void configure( ApplicationContext context ) {
      activatedDuringConfigure = getActivatable( context ).activated();
    }

    public void reset( ApplicationContext context ) {
      deactivatedDuringReset = getActivatable( context ).deactivated();
    }
    
    boolean isActivatedDuringConfigure() {
      return activatedDuringConfigure;
    }

    boolean isDeactivatedDuringReset() {
      return deactivatedDuringReset;
    }
    
    TestActivatable getActivatable() {
      return activatable;
    }

    private TestActivatable getActivatable( ApplicationContext context ) {
      activatable = ( TestActivatable )context.getInstance( TestActivatable.class );
      return activatable;
    }
  }
  

  public void testStateAfterCreation() {
    assertFalse( context.isActivated() );
    checkUnallowedMethodAccessIfNotActivated();
  }
  
  public void testActivate() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );

    context.activate();
    
    assertTrue( context.isActivated() );
    assertSame( context, configurable.getConfigureContext() );
    assertTrue( configurable.isActivatedOnConfigure() );
    checkUnallowedMethodAccessIfActivated();
  }

  public void testApplicationContextDeactivate() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );
    context.activate();

    context.deactivate();
    
    assertFalse( context.isActivated() );
    assertSame( context, configurable.getResetContext() );
    assertTrue( configurable.isActivatedOnReset() );
    checkUnallowedMethodAccessIfNotActivated();
  }
    
  public void testAddConfigurableWithNullParam() {
    try {
      context.addConfigurable( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testRemoveConfigurable() {
    TestConfigurable configurable = new TestConfigurable();
    context.addConfigurable( configurable );
    
    context.removeConfigurable( configurable );
    context.activate();
    
    assertNull( configurable.getConfigureContext() );
  }
  
  public void testRemoveConfigurableWithNullParam() {
    try {
      context.removeConfigurable( null );
      fail();
    } catch( NullPointerException expected ) {
    }
  }
  
  public void testActivatableNofifications() {
    context = new ApplicationContext( new Class[] { TestActivatable.class } );
    TestActivationOrderConfigurable configurable = new TestActivationOrderConfigurable();
    context.addConfigurable( configurable );
    
    context.activate();
    context.deactivate();
    
    assertFalse( configurable.isActivatedDuringConfigure() );
    assertTrue( configurable.getActivatable().activated() );
    assertTrue( configurable.isDeactivatedDuringReset() );
    assertTrue( configurable.getActivatable().deactivated() );
  }
  
  protected void setUp() throws Exception {
    context = new ApplicationContext();
  }

  private void checkUnallowedMethodAccessIfNotActivated() {
    try {
      context.getInstance( null );
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      context.deactivate();
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
  
  private void checkUnallowedMethodAccessIfActivated() {
    try {
      context.activate();
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      context.addConfigurable( null );
      fail();
    } catch( IllegalStateException expected ) {
    }
    try {
      context.removeConfigurable( null );
      fail();
    } catch( IllegalStateException expected ) {
    }
  }
}