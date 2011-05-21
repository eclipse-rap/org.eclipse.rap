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

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.theme.ThemeManagerHolder;
import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
import org.eclipse.rwt.service.ISessionStore;
import org.eclipse.swt.internal.widgets.DisplaysHolder;
import org.eclipse.swt.widgets.Display;


public class AdapterManagerConfigurable_Test extends TestCase {
  private static final String SPLIT = RWTServletContextListener.PARAMETER_SPLIT;
  
  private AdapterManagerConfigurable configurable;
  private ApplicationContext applicationContext;
  
  public static class TestAdapterFactory implements AdapterFactory {
    public Object getAdapter( Object adaptable, Class adapter ) {
      return new TestAdapter() {};
    }

    public Class[] getAdapterList() {
      return new Class[] { TestAdapter.class };
    }
  }
  
  public static class TestAdaptable implements Adaptable  {
    public Object getAdapter( Class adapter ) {
      return null;
    }
  }
  
  public interface TestAdapter {}

  public void testConfigure() {
    setInitParameter( getValidFactoryAndAdaptableValuePair() );
    
    applicationContext.activate();
    
    assertTrue( getTestAdapter() instanceof TestAdapter );
  }
  
  public void testConfigureWithDefaultSettings() {
    applicationContext.activate();
    
    checkAdapterIsInstanceOfILifeCycleAdapter();
  }
  
  public void testConfigureWithAdapterFactoryOnly() {
    setInitParameter( TestAdapterFactory.class.getName() );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithTooManyClasses() {
    setInitParameter( getTooManyClassesParameterValue() );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithUnknownAdapterFactory() {
    setInitParameter( getParameterValuePairWithUnknownFactory() );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithUnknownAdaptable() {
    setInitParameter( getParameterValuePairWithUnknownAdaptable() );
    
    try {
      applicationContext.activate();
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private String getParameterValuePairWithUnknownAdaptable() {
    return TestAdapterFactory.class.getName() + SPLIT + "unknown";
  }
  
  public void testReset() {
    setInitParameter( getValidFactoryAndAdaptableValuePair() );
    applicationContext.activate();
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    
    applicationContext.deactivate();
    
    assertNull( adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class ) );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new AdapterManagerConfigurable( servletContext );
    Class[] instanceTypes = new Class[] {
      AdapterManager.class, 
      DisplaysHolder.class, 
      ThemeManagerHolder.class
    };
    applicationContext = new ApplicationContext( instanceTypes );
    applicationContext.addConfigurable( configurable );
  }
  
  protected void tearDown() {
    setInitParameter( null );
  }
  
  private void checkAdapterIsInstanceOfILifeCycleAdapter() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object adapter = adapterManager.getAdapter( createDisplay(), ILifeCycleAdapter.class );
    assertTrue( adapter instanceof ILifeCycleAdapter );
  }

  private void setInitParameter( String value ) {
    Fixture.setInitParameter( RWTServletContextListener.ADAPTER_FACTORIES_PARAM, value );
  }
  
  private String getValidFactoryAndAdaptableValuePair() {
    return TestAdapterFactory.class.getName() + SPLIT + TestAdaptable.class.getName();
  }
  
  private String getTooManyClassesParameterValue() {
    String factoryName = TestAdapterFactory.class.getName();
    String adaptableName = TestAdaptable.class.getName();
    String surplusName = TestAdapter.class.getName();
    return factoryName + SPLIT + adaptableName + SPLIT + surplusName;
  }
  
  private String getParameterValuePairWithUnknownFactory() {
    return "unknown" + SPLIT + TestAdaptable.class.getName();
  }
  
  private Object getTestAdapter() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    return adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class );
  }

  private Display createDisplay() {
    Fixture.createServiceContext();
    ISessionStore session = ContextProvider.getSession();
    ApplicationContextUtil.registerApplicationContext( session, applicationContext );
    Display result = new Display();
    Fixture.disposeOfServiceContext();
    return result;
  }
}