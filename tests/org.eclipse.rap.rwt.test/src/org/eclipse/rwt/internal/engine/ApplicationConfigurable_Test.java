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

import java.io.IOException;

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rwt.*;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.engine.Context;
import org.eclipse.rwt.engine.Configurator;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.engine.ThemeManagerHelper.TestThemeManager;
import org.eclipse.rwt.internal.engine.configurables.RWTConfigurationConfigurable;
import org.eclipse.rwt.internal.lifecycle.IDisplayLifeCycleAdapter;
import org.eclipse.rwt.internal.lifecycle.UICallBackServiceHandler;
import org.eclipse.rwt.internal.resources.JSLibraryServiceHandler;
import org.eclipse.rwt.internal.service.MemorySettingStore;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.rwt.service.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class ApplicationConfigurable_Test extends TestCase {
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String STYLE_SHEET_2 = "resources/theme/TestExample2.css";

  private TestPhaseListener testPhaseListener;
  private TestSettingStoreFactory testSettingStoreFactory;
  private String entryPointName;
  private TestAdapterFactory testAdapterFactory;
  private TestResource testResource;
  private TestServiceHandler testServiceHandler;
  private String testServiceHandlerId;
  private TestBranding testBranding;
  private ApplicationContext applicationContext;
  private Display display;


  private static class TestPhaseListener implements PhaseListener {
    private static final long serialVersionUID = 1L;

    public void beforePhase( PhaseEvent event ) {
    }

    public void afterPhase( PhaseEvent event ) {
    }

    public PhaseId getPhaseId() {
      return null;
    }
  }
  
  private static class TestSettingStoreFactory implements ISettingStoreFactory {
    public ISettingStore createSettingStore( String storeId ) {
      return new MemorySettingStore( "" );
    }
  }
  
  private static class TestEntryPoint implements IEntryPoint {
    public int createUI() {
      return 0;
    }
  }
  
  private static class TestAdapterFactory implements AdapterFactory {
    public Object getAdapter( Object adaptable, Class adapter ) {
      return new TestAdapter() {};
    }

    public Class[] getAdapterList() {
      return new Class[] { TestAdapter.class };
    }
  }
  
  private static class TestAdaptable implements Adaptable  {
    public Object getAdapter( Class adapter ) {
      return null;
    }
  }
  
  private interface TestAdapter {}
  
  private class TestResource implements IResource {

    public ClassLoader getLoader() {
      return null;
    }

    public String getLocation() {
      return null;
    }

    public String getCharset() {
      return null;
    }

    public RegisterOptions getOptions() {
      return null;
    }

    public boolean isJSLibrary() {
      return false;
    }

    public boolean isExternal() {
      return false;
    }
  }
  
  private static class TestServiceHandler implements IServiceHandler {
    public void service() throws IOException, ServletException {
    }
  }
  
  private static class TestBranding extends AbstractBranding {}
  
  private static class TestWidget extends Composite {
    private static final long serialVersionUID = 1L;

    TestWidget( Composite parent ) {
      super( parent, SWT.NONE );
    }
  }
  
  public void testConfigure() {
    runConfigurator( createConfigurator() );
    
    checkContextDirectoryHasBeenSet();
    checkPhaseListenersHaveBeenAdded();
    checkSettingStoreManagerHasBeenSet();
    checkEntryPointHasBeenAdded();
    checkAdapterFactoriesHaveBeenAdded();
    checkResourceHasBeenAdded();
    checkServiceHandlersHaveBeenAdded();
    checkBrandingHasBeenAdded();
    checkThemeHasBeenAdded();
    checkThemableWidgetHasBeenAdded();
    checkThemeContributionHasBeenAdded();
  }
  
  public void testConfigureWithDefaultSettingStoreFactory() {
    runConfigurator( new Configurator() {
      public void configure( Context application ) {
        application.addTheme( THEME_ID, STYLE_SHEET );
      }
    } );
    
    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }
  
  public void testReset() {
    runConfigurator( createConfigurator() );
    
    applicationContext.deactivate();
    
    checkAdapterFactoriesHaveBeenRemoved();
    checkBrandingHasBeenRemoved();
    checkEntryPointHasBeenRemoved();
    checkPhaseListenerHasBeenRemoved();
    checkResourceHasBeenRemoved();
    checkConfigurationHasBeenResetted();
    checkServiceHandlerHasBeenRemoved();
    checkSettingStoreFactoryHasBeenRemoved();
    checkThemeManagerHasBeenResetted();
  }

  @Override
  protected void setUp() {
    applicationContext = new ApplicationContext();
    applicationContext.addConfigurable( createConfigurationConfigurable() );
    createDisplay();
    testPhaseListener = new TestPhaseListener();
    testSettingStoreFactory = new TestSettingStoreFactory();
    entryPointName = "entryPoint";
    testAdapterFactory = new TestAdapterFactory();
    testResource = new TestResource();
    testServiceHandler = new TestServiceHandler();
    testServiceHandlerId = "testServiceHandlerId";
    testBranding = new TestBranding();
  }

  private RWTConfigurationConfigurable createConfigurationConfigurable() {
    return new RWTConfigurationConfigurable( new TestServletContext() );
  }

  private void createDisplay() {
    TestServletContext servletContext = Fixture.createServletContext();
    Fixture.createServiceContext();
    ApplicationContextUtil.set( servletContext, applicationContext );
    display = new Display();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfServletContext();
  }

  private void runConfigurator( Configurator configurator ) {
    applicationContext.addConfigurable( new ApplicationConfigurable( configurator ) );
    applicationContext.activate();
  }

  private Configurator createConfigurator() {
    return new Configurator() {
      public void configure( Context application ) {
        application.addEntryPoint( entryPointName, TestEntryPoint.class );
        application.addResource( testResource );
        application.addPhaseListener( testPhaseListener );
        application.addAddapterFactory( TestAdaptable.class, testAdapterFactory );
        application.setSettingStoreFactory( testSettingStoreFactory );
        application.addServiceHandler( testServiceHandlerId, testServiceHandler );
        application.addBranding( testBranding );
        application.addTheme( THEME_ID, STYLE_SHEET );
        application.addThemableWidget( TestWidget.class );
        application.addThemeContribution( THEME_ID, STYLE_SHEET_2 );
      }
    };
  }

  private void checkThemeContributionHasBeenAdded() {
    Theme theme = applicationContext.getThemeManager().getTheme( THEME_ID );
    assertEquals( 18, theme.getValuesMap().getAllValues().length );
  }

  private void checkThemableWidgetHasBeenAdded() {
    assertNotNull( applicationContext.getThemeManager().getThemeableWidget( TestWidget.class ) );
  }

  private void checkThemeHasBeenAdded() {
    assertNotNull( applicationContext.getThemeManager().getTheme( THEME_ID ) );
  }

  private void checkBrandingHasBeenAdded() {
    assertEquals( 1, applicationContext.getBrandingManager().getAll().length );
    assertSame( testBranding, applicationContext.getBrandingManager().getAll()[ 0 ] );
  }

  private void checkServiceHandlersHaveBeenAdded() {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    assertSame( testServiceHandler, serviceManager.getCustomHandler( testServiceHandlerId ) );
    assertNotNull( serviceManager.getCustomHandler( UICallBackServiceHandler.HANDLER_ID ) );
    assertNotNull( serviceManager.getCustomHandler( JSLibraryServiceHandler.HANDLER_ID ) );
  }

  private void checkResourceHasBeenAdded() {
    assertEquals( 1, applicationContext.getResourceRegistry().get().length );
    assertSame( testResource, applicationContext.getResourceRegistry().get()[ 0 ] );
  }

  private void checkAdapterFactoriesHaveBeenAdded() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object testAdapter = adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class );
    Object displayAdapter = adapterManager.getAdapter( display, ILifeCycleAdapter.class );
    assertTrue( testAdapter instanceof TestAdapter );
    assertTrue( displayAdapter instanceof IDisplayLifeCycleAdapter );
  }

  private void checkEntryPointHasBeenAdded() {
    assertEquals( 1, applicationContext.getEntryPointManager().getEntryPoints().length );
  }

  private void checkSettingStoreManagerHasBeenSet() {
    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkPhaseListenersHaveBeenAdded() {
    assertEquals( 3, applicationContext.getPhaseListenerRegistry().get().length );
  }

  private void checkContextDirectoryHasBeenSet() {
    RWTConfiguration configuration = applicationContext.getConfiguration();
    assertEquals( Fixture.WEB_CONTEXT_DIR, configuration.getContextDirectory() );
  }
  
  private void checkAdapterFactoriesHaveBeenRemoved() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object testAdapter = adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class );
    assertNull( testAdapter );
  }

  private void checkBrandingHasBeenRemoved() {
    assertEquals( 0, applicationContext.getBrandingManager().getAll().length );
  }

  private void checkEntryPointHasBeenRemoved() {
    assertEquals( 0, applicationContext.getEntryPointManager().getEntryPoints().length );
  }

  private void checkPhaseListenerHasBeenRemoved() {
    assertEquals( 0, applicationContext.getPhaseListenerRegistry().get().length );
  }

  private void checkResourceHasBeenRemoved() {
    assertEquals( 0, applicationContext.getResourceRegistry().get().length );
  }

  private void checkConfigurationHasBeenResetted() {
    assertFalse( ( ( RWTConfigurationImpl )applicationContext.getConfiguration() ).isConfigured() );
  }

  private void checkServiceHandlerHasBeenRemoved() {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    assertNull( serviceManager.getCustomHandler( testServiceHandlerId ) );
  }

  private void checkSettingStoreFactoryHasBeenRemoved() {
    assertFalse( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkThemeManagerHasBeenResetted() {
    TestThemeManager themeManager = ( TestThemeManager )applicationContext.getThemeManager();
    assertEquals( 0, themeManager.getRegisteredThemeIds().length );
  }
}