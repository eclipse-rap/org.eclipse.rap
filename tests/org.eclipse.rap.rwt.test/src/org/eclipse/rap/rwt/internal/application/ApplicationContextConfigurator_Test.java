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

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.internal.lifecycle.DefaultEntryPointFactory;
import org.eclipse.rap.rwt.internal.lifecycle.PhaseListenerRegistry;
import org.eclipse.rap.rwt.internal.lifecycle.TestEntryPoint;
import org.eclipse.rap.rwt.internal.resources.ResourceDirectory;
import org.eclipse.rap.rwt.internal.service.ServiceManagerImpl;
import org.eclipse.rap.rwt.internal.textsize.MeasurementListener;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.uicallback.UICallBackServiceHandler;
import org.eclipse.rap.rwt.lifecycle.PhaseListener;
import org.eclipse.rap.rwt.service.ISettingStoreFactory;
import org.eclipse.rap.rwt.service.ResourceLoader;
import org.eclipse.rap.rwt.service.ServiceHandler;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper.TestThemeManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


public class ApplicationContextConfigurator_Test extends TestCase {

  private static final String TEST_RESOURCE = "test-resource";
  private static final Object ATTRIBUTE_VALUE = new Object();
  private static final String ATTRIBUTE_NAME = "name";
  private static final String THEME_ID = "TestTheme";
  private static final String STYLE_SHEET = "resources/theme/TestExample.css";
  private static final String STYLE_SHEET_CONTRIBUTION = "resources/theme/TestExample2.css";

  private PhaseListener phaseListener;
  private ISettingStoreFactory settingStoreFactory;
  private ServiceHandler serviceHandler;
  private String serviceHandlerId;
  private ApplicationContextImpl applicationContext;
  private File tempDirectory;

  @Override
  protected void setUp() throws IOException {
    tempDirectory = createTempDirectory();
    phaseListener = mock( PhaseListener.class );
    settingStoreFactory = mock( ISettingStoreFactory.class );
    serviceHandler = mock( ServiceHandler.class );
    serviceHandlerId = "serviceHandlerId";
  }

  @Override
  protected void tearDown() throws Exception {
    Fixture.delete( tempDirectory );
  }

  public void testConfigure() {
    activateApplicationContext( createConfiguration() );

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
    activateApplicationContext( createConfiguration(), tempDirectory );

    checkContextDirectoryHasBeenSet( tempDirectory );
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
    activateApplicationContext( createConfiguration() );

    applicationContext.deactivate();

    checkEntryPointsHaveBeenRemoved();
    checkPhaseListenerHasBeenRemoved();
    checkResourceHasBeenRemoved();
    checkConfigurationHasBeenReset();
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
    applicationContext = new ApplicationContextImpl( configuration, servletContext );
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

  private File createTempDirectory() throws IOException {
    File result = File.createTempFile( "applicationContextConfigurationTest", ".tmp" );
    result.delete();
    result.mkdir();
    return result;
  }

  private void createDisplay() {
    Fixture.createServiceContext();
    Fixture.disposeOfServiceContext();
    Fixture.disposeOfServletContext();
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
        configuration.addServiceHandler( serviceHandlerId, serviceHandler );
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
    assertSame( serviceHandler, serviceManager.getServiceHandler( serviceHandlerId ) );
    assertNotNull( serviceManager.getServiceHandler( UICallBackServiceHandler.HANDLER_ID ) );
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
    assertNull( serviceManager.getServiceHandler( serviceHandlerId ) );
  }

  private void checkSettingStoreFactoryHasBeenRemoved() {
    assertFalse( applicationContext.getSettingStoreManager().hasFactory() );
  }

  private void checkThemeManagerHasBeenResetted() {
    TestThemeManager themeManager = ( TestThemeManager )applicationContext.getThemeManager();
    assertEquals( 0, themeManager.getRegisteredThemeIds().length );
  }

  private void checkApplicationStoreHasBeenResetted() {
    Object attribute = applicationContext.getAttribute( ATTRIBUTE_NAME );
    assertNull( attribute );
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
