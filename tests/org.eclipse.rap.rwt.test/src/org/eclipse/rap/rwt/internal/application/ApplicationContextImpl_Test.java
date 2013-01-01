/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Appel and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.serverpush.ServerPushServiceHandler;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.service.SettingStoreFactory;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper.TestThemeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Test;


public class ApplicationContextImpl_Test {
  private static final String TEST_RESOURCE = "test-resource";
  private static final Object ATTRIBUTE_VALUE = new Object();
  private static final String ATTRIBUTE_NAME = "name";
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String STYLE_SHEET_CONTRIBUTION = "resources/theme/TestExample2.css";
  private static final String SERVICE_HANDLER_ID = "SERVICE_HANDLER_ID";
  private PhaseListener phaseListener;
  private SettingStoreFactory settingStoreFactory;
  private ServiceHandler serviceHandler;
  private ApplicationContextImpl applicationContext;

  @Before
  public void setUp() {
    phaseListener = mock( PhaseListener.class );
    settingStoreFactory = mock( SettingStoreFactory.class );
    serviceHandler = mock( ServiceHandler.class );
  }

  @Test
  public void testApplicationContextSingletons() {
    applicationContext = new ApplicationContextImpl( null, null );

    assertNotNull( applicationContext.getThemeManager() );
    assertSame( applicationContext.getThemeManager(), applicationContext.getThemeManager() );

    assertNotNull( applicationContext.getPhaseListenerRegistry() );
    assertSame( applicationContext.getPhaseListenerRegistry(), applicationContext.getPhaseListenerRegistry() );

    assertNotNull( applicationContext.getLifeCycleFactory() );
    assertSame( applicationContext.getLifeCycleFactory(), applicationContext.getLifeCycleFactory() );

    assertNotNull( applicationContext.getEntryPointManager() );
    assertSame( applicationContext.getEntryPointManager(), applicationContext.getEntryPointManager() );

    assertNotNull( applicationContext.getResourceFactory() );
    assertSame( applicationContext.getResourceFactory(), applicationContext.getResourceFactory() );

    assertNotNull( applicationContext.getImageFactory() );
    assertSame( applicationContext.getImageFactory(), applicationContext.getImageFactory() );

    assertNotNull( applicationContext.getInternalImageFactory() );
    assertSame( applicationContext.getInternalImageFactory(), applicationContext.getInternalImageFactory() );

    assertNotNull( applicationContext.getImageDataFactory() );
    assertSame( applicationContext.getImageDataFactory(), applicationContext.getImageDataFactory() );

    assertNotNull( applicationContext.getFontDataFactory() );
    assertSame( applicationContext.getFontDataFactory(), applicationContext.getFontDataFactory() );

    assertNotNull( applicationContext.getSettingStoreManager() );
    assertSame( applicationContext.getSettingStoreManager(), applicationContext.getSettingStoreManager() );

    assertNotNull( applicationContext.getServiceManager() );
    assertSame( applicationContext.getServiceManager(), applicationContext.getServiceManager() );

    assertNotNull( applicationContext.getResourceRegistry() );
    assertSame( applicationContext.getResourceRegistry(), applicationContext.getResourceRegistry() );

    assertNotNull( applicationContext.getResourceDirectory() );
    assertSame( applicationContext.getResourceDirectory(), applicationContext.getResourceDirectory() );

    assertNotNull( applicationContext.getResourceManager() );
    assertSame( applicationContext.getResourceManager(), applicationContext.getResourceManager() );

    assertNotNull( applicationContext.getStartupPage() );
    assertSame( applicationContext.getStartupPage(), applicationContext.getStartupPage() );

    assertNotNull( applicationContext.getDisplaysHolder() );
    assertSame( applicationContext.getDisplaysHolder(), applicationContext.getDisplaysHolder() );

    assertNotNull( applicationContext.getTextSizeStorage() );
    assertSame( applicationContext.getTextSizeStorage(), applicationContext.getTextSizeStorage() );

    assertNotNull( applicationContext.getProbeStore() );
    assertSame( applicationContext.getProbeStore(), applicationContext.getProbeStore() );

    assertNotNull( applicationContext.getClientSelector() );
    assertSame( applicationContext.getClientSelector(), applicationContext.getClientSelector() );
  }

  @Test
  public void testIsActive_isFalseAfterCreation() {
    applicationContext = new ApplicationContextImpl( null, null );

    assertFalse( applicationContext.isActive() );
  }

  @Test
  public void testActivate_initializesSubSystems() {
    activateApplicationContext( createConfiguration(), null );

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

  @Test
  public void testActivate_withDifferentResourceLocation() {
    File tempDirectory = createTempDirectory();

    activateApplicationContext( createConfiguration(), tempDirectory );

    checkContextDirectoryHasBeenSet( tempDirectory );
  }

  @Test
  public void testActivate_withDefaultSettingStoreFactory() {
    activateApplicationContext( new ApplicationConfiguration() {
      public void configure( Application application ) {
        application.addStyleSheet( THEME_ID, STYLE_SHEET );
      }
    }, null );

    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  @Test
  public void testDeactivate_failsIfNotActivated() {
    applicationContext = new ApplicationContextImpl( null, null );

    try {
      applicationContext.deactivate();
      fail();
    } catch( IllegalStateException expected ) {
      assertEquals( "The ApplicationContext has not been activated.", expected.getMessage() );
    }
  }

  @Test
  public void testDeactivate_resetsSubSystems() {
    activateApplicationContext( createConfiguration(), null );

    applicationContext.deactivate();

    checkEntryPointsHaveBeenRemoved();
    checkPhaseListenerHasBeenRemoved();
    checkResourceHasBeenRemoved();
    checkConfigurationHasBeenReset();
    checkServiceHandlerHasBeenRemoved();
    checkSettingStoreFactoryHasBeenRemoved();
    checkThemeManagerHasBeenReset();
    checkApplicationStoreHasBeenReset();
  }

  private void activateApplicationContext( ApplicationConfiguration configuration,
                                           File contextDirectory )
  {
    ServletContext servletContext = createServiceContext( contextDirectory );
    applicationContext = new ApplicationContextImpl( configuration, servletContext );
    activateApplicationContext( applicationContext );
  }

  private ServletContext createServiceContext( File contextDirectory ) {
    ServletContext servletContext = Fixture.createServletContext();
    setContextDirectory( servletContext, contextDirectory );
    return servletContext;
  }

  private void setContextDirectory( ServletContext servletContext, File contextDirectory ) {
    if( contextDirectory != null ) {
      servletContext.setAttribute( ApplicationConfiguration.RESOURCE_ROOT_LOCATION,
                                   contextDirectory.toString() );
    }
  }

  private File createTempDirectory() {
    File tempDir = new File( Fixture.TEMP_DIR, ApplicationContextImpl_Test.class.getName() );
    if( !tempDir.mkdir() ) {
      throw new RuntimeException( "Failed to create temp directory" );
    }
    return tempDir;
  }

  private ApplicationConfiguration createConfiguration() {
    return new ApplicationConfiguration() {
      public void configure( Application configuration ) {
        configuration.addEntryPoint( "/entryPoint", TestEntryPoint.class, null );
        DefaultEntryPointFactory factory = new DefaultEntryPointFactory( TestEntryPoint.class );
        configuration.addEntryPoint( "/entryPointViaFactory", factory, null );
        configuration.addResource( TEST_RESOURCE, new TestResourceLoader() );
        configuration.addPhaseListener( phaseListener );
        configuration.setSettingStoreFactory( settingStoreFactory );
        configuration.addServiceHandler( SERVICE_HANDLER_ID, serviceHandler );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET );
        configuration.addStyleSheet( THEME_ID, STYLE_SHEET_CONTRIBUTION );
        configuration.addThemableWidget( TestWidget.class );
        configuration.setAttribute( ATTRIBUTE_NAME, ATTRIBUTE_VALUE );
      }
    };
  }

  private void checkAttributeHasBeenSet() {
    Object attribute = applicationContext.getAttribute( ATTRIBUTE_NAME );
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
    ServiceManagerImpl serviceManager = applicationContext.getServiceManager();
    assertSame( serviceHandler, serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
    assertNotNull( serviceManager.getServiceHandler( ServerPushServiceHandler.HANDLER_ID ) );
  }

  private void checkResourceHasBeenAdded() {
    assertTrue( applicationContext.getResourceManager().isRegistered( TEST_RESOURCE ) );
  }

  private void checkEntryPointsHaveBeenAdded() {
    assertEquals( 2, applicationContext.getEntryPointManager().getServletPaths().size() );
  }

  private void checkSettingStoreManagerHasBeenSet() {
    assertTrue( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkPhaseListenersHaveBeenAdded() {
    PhaseListenerRegistry phaseListenerRegistry = applicationContext.getPhaseListenerRegistry();

    List<PhaseListener> registeredPhaseListeners = Arrays.asList( phaseListenerRegistry.getAll() );

    assertEquals( 2, registeredPhaseListeners.size() );
    assertTrue( registeredPhaseListeners.contains( phaseListener ) );
    assertTrue( containsType( registeredPhaseListeners, MeasurementListener.class ) );
  }

  private void checkContextDirectoryHasBeenSet() {
    File webContextDir = Fixture.WEB_CONTEXT_DIR;
    checkContextDirectoryHasBeenSet( webContextDir );
  }

  private void checkContextDirectoryHasBeenSet( File contextDirectory ) {
    ResourceDirectory resourceDirectory = applicationContext.getResourceDirectory();
    assertEquals( contextDirectory, resourceDirectory.getDirectory().getParentFile() );
  }

  private void checkEntryPointsHaveBeenRemoved() {
    assertEquals( 0, applicationContext.getEntryPointManager().getServletPaths().size() );
  }

  private void checkPhaseListenerHasBeenRemoved() {
    assertEquals( 0, applicationContext.getPhaseListenerRegistry().getAll().length );
  }

  private void checkResourceHasBeenRemoved() {
    assertEquals( 0, applicationContext.getResourceRegistry().getResourceRegistrations().length );
  }

  private void checkConfigurationHasBeenReset() {
    try {
      applicationContext.getResourceDirectory().getDirectory();
      fail();
    } catch( IllegalStateException exception ) {
    }
  }

  private void checkServiceHandlerHasBeenRemoved() {
    ServiceManagerImpl serviceManager = applicationContext.getServiceManager();
    assertNull( serviceManager.getServiceHandler( SERVICE_HANDLER_ID ) );
  }

  private void checkSettingStoreFactoryHasBeenRemoved() {
    assertFalse( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkThemeManagerHasBeenReset() {
    TestThemeManager themeManager = ( TestThemeManager )applicationContext.getThemeManager();
    assertEquals( 0, themeManager.getRegisteredThemeIds().length );
  }

  private void checkApplicationStoreHasBeenReset() {
    Object attribute = applicationContext.getAttribute( ATTRIBUTE_NAME );
    assertNull( attribute );
  }

  private static void activateApplicationContext( ApplicationContextImpl applicationContext ) {
    ApplicationContextUtil.set( Fixture.getServletContext(), applicationContext );
    createDisplay();
    applicationContext.activate();
  }

  private static void createDisplay() {
    Fixture.createServiceContext();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfServletContext();
  }

  private static boolean containsType( List<?> list, Class<?> type ) {
    for( Object object : list ) {
      if( object.getClass().equals( type ) ) {
        return true;
      }
    }
    return false;
  }

  private static class TestWidget extends Composite {
    TestWidget( Composite parent ) {
      super( parent, SWT.NONE );
    }
  }

  private static class TestResourceLoader implements ResourceLoader {
    public InputStream getResourceAsStream( String resourceName ) throws IOException {
      return mock( InputStream.class );
    }
  }

}
