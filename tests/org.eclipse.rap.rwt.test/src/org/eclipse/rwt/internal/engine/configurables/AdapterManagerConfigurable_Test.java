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
package org.eclipse.rwt.internal.engine.configurables;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.lifecycle.ILifeCycleAdapter;
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
    
    configurable.configure( applicationContext );
    
    assertTrue( getTestAdapter() instanceof TestAdapter );
  }
  
  public void testConfigureWithDefaultSettings() {
    configurable.configure( applicationContext );
    
    checkAdapterIsInstanceOfILifeCycleAdapter();
  }
  
  public void testConfigureWithAdapterFactoryOnly() {
    setInitParameter( TestAdapterFactory.class.getName() );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithTooManyClasses() {
    setInitParameter( getTooManyClassesParameterValue() );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithUnknownAdapterFactory() {
    setInitParameter( getParameterValuePairWithUnknownFactory() );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }
  
  public void testConfigureWithUnknownAdaptable() {
    setInitParameter( getParameterValuePairWithUnknownAdaptable() );
    
    try {
      configurable.configure( applicationContext );
      fail();
    } catch( IllegalArgumentException expected ) {
    }
  }

  private String getParameterValuePairWithUnknownAdaptable() {
    return TestAdapterFactory.class.getName() + SPLIT + "unknown";
  }
  
  public void testReset() {
    setInitParameter( getValidFactoryAndAdaptableValuePair() );
    configurable.configure( applicationContext );
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    
    configurable.reset( applicationContext );
    
    assertNull( adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class ) );
  }
  
  protected void setUp() {
    ServletContext servletContext = Fixture.createServletContext();
    configurable = new AdapterManagerConfigurable( servletContext );
    applicationContext = new ApplicationContext();
  }
  
  protected void tearDown() {
    setInitParameter( null );
    Fixture.disposeOfServletContext();
  }
  
  private void checkAdapterIsInstanceOfILifeCycleAdapter() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object adapter = adapterManager.getAdapter( createDisplay(), ILifeCycleAdapter.class );
    assertTrue( adapter instanceof ILifeCycleAdapter );
  }

  private void setInitParameter( String value ) {
    Fixture.setInitParameter( AdapterManagerConfigurable.ADAPTER_FACTORIES_PARAM, value );
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
    TestServletContext servletContext = Fixture.getServletContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    Display result = new Display();
    Fixture.disposeOfServiceContext();
    return result;
  }
}