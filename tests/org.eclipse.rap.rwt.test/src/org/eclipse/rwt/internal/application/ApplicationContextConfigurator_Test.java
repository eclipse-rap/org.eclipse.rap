/*******************************************************************************
 * Copyright (c) 2011, 2012 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.application;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper.TestThemeManager;
import org.eclipse.rap.rwt.testfixture.internal.service.MemorySettingStore;
import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.AdapterFactory;
import org.eclipse.rwt.application.ApplicationConfiguration;
import org.eclipse.rwt.application.ApplicationConfigurator;
import org.eclipse.rwt.internal.AdapterManager;
import org.eclipse.rwt.internal.engine.RWTConfiguration;
import org.eclipse.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rwt.internal.service.ServiceManager;
import org.eclipse.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rwt.internal.theme.Theme;
import org.eclipse.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rwt.lifecycle.IEntryPoint;
import org.eclipse.rwt.lifecycle.PhaseEvent;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.lifecycle.PhaseListener;
import org.eclipse.rwt.resources.IResource;
import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.rwt.service.IServiceHandler;
import org.eclipse.rwt.service.ISettingStore;
import org.eclipse.rwt.service.ISettingStoreFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


@SuppressWarnings("deprecation")
public class ApplicationContextConfigurator_Test extends TestCase {

  private static final Object ATTRIBUTE_VALUE = new Object();
  private static final String ATTRIBUTE_NAME = "name";
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String STYLE_SHEET_CONTRIBUTION = "resources/theme/TestExample2.css";

  private TestPhaseListener testPhaseListener;
  private TestSettingStoreFactory testSettingStoreFactory;
  private TestAdapterFactory testAdapterFactory;
  private TestResource testResource;
  private TestServiceHandler testServiceHandler;
  private String testServiceHandlerId;
  private ApplicationContext applicationContext;

  @Override
  protected void setUp() {
    testPhaseListener = new TestPhaseListener();
    testSettingStoreFactory = new TestSettingStoreFactory();
    testAdapterFactory = new TestAdapterFactory();
    testResource = new TestResource();
    testServiceHandler = new TestServiceHandler();
    testServiceHandlerId = "testServiceHandlerId";
  }

  public void testConfigure() {
    activateApplicationContext( createConfigurator() );

    checkContextDirectoryHasBeenSet();
    checkPhaseListenersHaveBeenAdded();
    checkSettingStoreManagerHasBeenSet();
    checkEntryPointsHaveBeenAdded();
    checkAdapterFactoriesHaveBeenAdded();
    checkResourceHasBeenAdded();
    checkServiceHandlersHaveBeenAdded();
    checkThemeHasBeenAdded();
    checkThemableWidgetHasBeenAdded();
    checkThemeContributionHasBeenAdded();
    checkAttributeHasBeenSet();
  }

  public void testConfigureWithDifferentResourceLocation() {
    File contextDirectory = createTmpFile();

    activateApplicationContext( createConfigurator(), contextDirectory );

    checkContextDirectoryHasBeenSet( contextDirectory );
  }

  public void testConfigureWithDefaultSettingStoreFactory() {
    activateApplicationContext( new ApplicationConfigurator() {
      public void configure( ApplicationConfiguration configuration ) {
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET );
      }
    } );

    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  public void testReset() {
    activateApplicationContext( createConfigurator() );

    applicationContext.deactivate();

    checkAdapterFactoriesHaveBeenRemoved();
    checkBrandingHasBeenRemoved();
    checkEntryPointsHaveBeenRemoved();
    checkPhaseListenerHasBeenRemoved();
    checkResourceHasBeenRemoved();
    checkConfigurationHasBeenResetted();
    checkServiceHandlerHasBeenRemoved();
    checkSettingStoreFactoryHasBeenRemoved();
    checkThemeManagerHasBeenResetted();
    checkApplicationStoreHasBeenResetted();
  }

  private void activateApplicationContext( ApplicationConfigurator configurator ) {
    activateApplicationContext( configurator, null );
  }

  private void activateApplicationContext( ApplicationConfigurator configurator,
                                           File contextDirectory )
  {
    ServletContext servletContext = Fixture.createServletContext();
    setContextDirectory( servletContext, contextDirectory );
    applicationContext = new ApplicationContext( configurator, servletContext );
    ApplicationContextUtil.set( Fixture.getServletContext(), applicationContext );
    createDisplay();
    applicationContext.activate();
  }

  private void setContextDirectory( ServletContext servletContext, File contextDirectory ) {
    if( contextDirectory != null ) {
      servletContext.setAttribute( ApplicationConfigurator.RESOURCE_ROOT_LOCATION,
                                   contextDirectory.toString() );
    }
  }

  private File createTmpFile() {
    try {
      return File.createTempFile( "applicationContextConfigurationTest", "tmp" );
    } catch( IOException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private void createDisplay() {
    Fixture.createServiceContext();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfServletContext();
  }

  private ApplicationConfigurator createConfigurator() {
    return new ApplicationConfigurator() {
      public void configure( ApplicationConfiguration configuration ) {
        configuration.addEntryPoint( "/entryPoint", TestEntryPoint.class );
        DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );
        configuration.addEntryPoint( "/entryPointViaFactory", factory );
        configuration.addResource( testResource );
        configuration.addPhaseListener( testPhaseListener );
        configuration.setSettingStoreFactory( testSettingStoreFactory );
        configuration.addServiceHandler( testServiceHandlerId, testServiceHandler );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET_CONTRIBUTION );
        configuration.addThemableWidget( TestWidget.class );
        configuration.setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );

        // Only supported for Workbench API backward compatibility
        ( ( ApplicationConfigurationImpl )configuration )
          .addAdapterFactory( TestAdaptable.class, testAdapterFactory );
      }
    };
  }

  private void checkAttributeHasBeenSet() {
    Object attribute = applicationContext.getApplicationStore().getAttribute( ATTRIBUTE_NAME );
    assertSame( ATTRIBUTE_VALUE, attribute );
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

  private void checkServiceHandlersHaveBeenAdded() {
    ServiceManager serviceManager = applicationContext.getServiceManager();
    assertSame( testServiceHandler, serviceManager.getCustomHandler( testServiceHandlerId ) );
    assertNotNull( serviceManager.getCustomHandler( UICallBackServiceHandler.HANDLER_ID ) );
  }

  private void checkResourceHasBeenAdded() {
    assertEquals( 1, applicationContext.getResourceRegistry().get().length );
    assertSame( testResource, applicationContext.getResourceRegistry().get()[ 0 ] );
  }

  private void checkAdapterFactoriesHaveBeenAdded() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object testAdapter = adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class );
    assertTrue( testAdapter instanceof TestAdapter );
  }

  private void checkEntryPointsHaveBeenAdded() {
    assertEquals( 2, applicationContext.getEntryPointManager().getServletPaths().size() );
  }

  private void checkSettingStoreManagerHasBeenSet() {
    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkPhaseListenersHaveBeenAdded() {
    assertEquals( 2, applicationContext.getPhaseListenerRegistry().getAll().length );
    assertEquals( true, findPhaseListener( MeasurementListener.class ) );
    assertEquals( true, findPhaseListener( TestPhaseListener.class ) );
  }

  private void checkContextDirectoryHasBeenSet() {
    File webContextDir = Fixture.WEB_CONTEXT_DIR;
    checkContextDirectoryHasBeenSet( webContextDir );
  }

  private void checkContextDirectoryHasBeenSet( File contextDirectory ) {
    RWTConfiguration rwtConfiguration = applicationContext.getConfiguration();
    assertEquals( contextDirectory, rwtConfiguration.getContextDirectory() );
  }

  private void checkAdapterFactoriesHaveBeenRemoved() {
    AdapterManager adapterManager = applicationContext.getAdapterManager();
    Object testAdapter = adapterManager.getAdapter( new TestAdaptable(), TestAdapter.class );
    assertNull( testAdapter );
  }

  private void checkBrandingHasBeenRemoved() {
    assertEquals( 0, applicationContext.getBrandingManager().getAll().length );
  }

  private void checkEntryPointsHaveBeenRemoved() {
    assertEquals( 0, applicationContext.getEntryPointManager().getServletPaths().size() );
  }

  private void checkPhaseListenerHasBeenRemoved() {
    assertEquals( 0, applicationContext.getPhaseListenerRegistry().getAll().length );
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

  private void checkApplicationStoreHasBeenResetted() {
    Object attribute = applicationContext.getApplicationStore().getAttribute( ATTRIBUTE_NAME );
    assertNull( attribute );
  }

  private boolean findPhaseListener( Class phaseListenerClass ) {
    boolean result = false;
    PhaseListener[] phaseListeners = applicationContext.getPhaseListenerRegistry().getAll();
    for( int i = 0; !result && i < phaseListeners.length; i++ ) {
      if( phaseListeners[ i ].getClass().equals( phaseListenerClass  ) ) {
        result = true;
      }
    }
    return result;
  }

  private static class TestPhaseListener implements PhaseListener {
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
    public <T> T getAdapter( Class<T> adapter ) {
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
      return true;
    }
  }

  private static class TestServiceHandler implements IServiceHandler {
    public void service() throws IOException, ServletException {
    }
  }

  private static class TestWidget extends Composite {
    TestWidget( Composite parent ) {
      super( parent, SWT.NONE );
    }
  }
}