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

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.Fixture;
import org.eclipse.rwt.internal.lifecycle.EntryPointManager;
import org.eclipse.rwt.lifecycle.IEntryPoint;


public class EntryPointManagerConfigurable_Test extends TestCase {
  private static final String SEPARATOR = RWTServletContextListener.PARAMETER_SEPARATOR;
  private static final String SPLIT = RWTServletContextListener.PARAMETER_SPLIT;
  
  private EntryPointManagerConfigurable configurable;
  private ApplicationContext applicationContext;

  public static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }
  
  public void testConfigure() {
    setDefaultEntryPointInitParameter();
    
    applicationContext.activate();
    
    assertEquals( 1, getEntryPoints().length );
    assertEquals( EntryPointManager.DEFAULT, getEntryPoints()[ 0 ] );
  }

  public void testConfigureWithEntryPointName() {
    String entryPointName = "entryPointName";
    setEntryPointInitParameter( entryPointName );
    
    applicationContext.activate();
    
    assertEquals( 1, getEntryPoints().length );
    assertEquals( entryPointName, getEntryPoints()[ 0 ] );
  }

  public void testConfigureWithMultipleEntryPoints() {
    setMultipleEntryPointsInitParameter();
    
    applicationContext.activate();
    
    assertEquals( 2, getEntryPoints().length );
  }
  
  public void testConfigureWithUnknownEntryPointClass() {
    Fixture.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, "unknown" );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testReset() {
    setDefaultEntryPointInitParameter();
    applicationContext.activate();
    EntryPointManager entryPointManager = applicationContext.getEntryPointManager();
    
    applicationContext.deactivate();
    
    assertEquals( 0, entryPointManager.getEntryPoints().length );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new EntryPointManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext( new Class[] { EntryPointManager.class } );
    applicationContext.addConfigurable( configurable );
  }
  
  protected void tearDown() {
    Fixture.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, null );
  }

  private void setDefaultEntryPointInitParameter() {
    String entryPointClassName = TestEntryPoint.class.getName();
    setEntryPointsInitParameter( entryPointClassName );
  }
  
  private void setEntryPointInitParameter( String entryPointName ) {
    String entryPointClassName = TestEntryPoint.class.getName();
    String value = entryPointClassName + SPLIT + entryPointName;
    setEntryPointsInitParameter( value );
  }

  private void setMultipleEntryPointsInitParameter() {
    String entryPointClassName = TestEntryPoint.class.getName();
    String entryPointName = "entryPointName";
    String value = entryPointClassName + SPLIT + entryPointName + SEPARATOR + entryPointClassName;
    setEntryPointsInitParameter( value );
  }

  private void setEntryPointsInitParameter( String value ) {
    Fixture.setInitParameter( RWTServletContextListener.ENTRY_POINTS_PARAM, value );
  }

  private String[] getEntryPoints() {
    return applicationContext.getEntryPointManager().getEntryPoints();
  }
}
