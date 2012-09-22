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
package org.eclipse.rap.rwt.internal.application;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.application.ApplicationContextUtil;
import org.eclipse.rap.rwt.internal.engine.RWTConfiguration;
import org.eclipse.rap.rwt.internal.engine.RWTConfigurationImpl;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.internal.service.ServiceManager;
import org.eclipse.rap.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rap.rwt.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.resources.IResource;
import org.eclipse.rap.rwt.resources.IResourceManager.RegisterOptions;
import org.eclipse.rap.rwt.service.IServiceHandler;
import org.eclipse.rap.rwt.service.ISettingStore;
import org.eclipse.rap.rwt.service.ISettingStoreFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper.TestThemeManager;
import org.eclipse.rap.rwt.testfixture.internal.service.MemorySettingStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class ApplicationContextConfigurator_Test extends TestCase {

  private static final Object ATTRIBUTE_VALUE = new Object();
  private static final String ATTRIBUTE_NAME = "name";
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String STYLE_SHEET_CONTRIBUTION = "resources/theme/TestExample2.css";

  private TestPhaseListener testPhaseListener;
  private TestSettingStoreFactory testSettingStoreFactory;
  private TestResource testResource;
  private TestServiceHandler testServiceHandler;
  private String testServiceHandlerId;
  private ApplicationContext applicationContext;

  @Override
  protected void setUp() {
    testPhaseListener = new TestPhaseListener();
    testSettingStoreFactory = new TestSettingStoreFactory();
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
    activateApplicationContext( new ApplicationConfiguration() {
      public void configure( Application application ) {
        application.addStyleSheet( THEME_ID, STYLE_SHEET );
      }
    } );

    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  public void testReset() {
    activateApplicationContext( createConfigurator() );

    applicationContext.deactivate();

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

  private void activateApplicationContext( ApplicationConfiguration configuration ) {
    activateApplicationContext( configuration, null );
  }

  private void activateApplicationContext( ApplicationConfiguration configuration,
                                           File contextDirectory )
  {
    ServletContext servletContext = Fixture.createServletContext();
    setContextDirectory( servletContext, contextDirectory );
    applicationContext = new ApplicationContext( configuration, servletContext );
    ApplicationContextUtil.set( Fixture.getServletContext(), applicationContext );
    createDisplay();
    applicationContext.activate();
  }

  private void setContextDirectory( ServletContext servletContext, File contextDirectory ) {
    if( contextDirectory != null ) {
      servletContext.setAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION,
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

  private ApplicationConfiguration createConfigurator() {
    return new ApplicationConfiguration() {
      public void configure( Application configuration ) {
        configuration.addEntryPoint( "/entryPoint", TestEntryPoint.class, null );
        DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );
        configuration.addEntryPoint( "/entryPointViaFactory", factory, null );
        configuration.addResource( testResource );
        configuration.addPhaseListener( testPhaseListener );
        configuration.setSettingStoreFactory( testSettingStoreFactory );
        configuration.addServiceHandler( testServiceHandlerId, testServiceHandler );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET_CONTRIBUTION );
        configuration.addThemableWidget( TestWidget.class );
        configuration.setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
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
    assertSame( testServiceHandler, serviceManager.getServiceHandler( testServiceHandlerId ) );
    assertNotNull( serviceManager.getServiceHandler( UICallBackServiceHandler.HANDLER_ID ) );
  }

  private void checkResourceHasBeenAdded() {
    assertEquals( 1, applicationContext.getResourceRegistry().get().length );
    assertSame( testResource, applicationContext.getResourceRegistry().get()[ 0 ] );
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
    assertNull( serviceManager.getServiceHandler( testServiceHandlerId ) );
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
