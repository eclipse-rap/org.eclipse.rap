/*******************************************************************************
 * Copyright (c) 2011, 2015 Frank Appel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Frank Appel - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.osgi.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.rap.rwt.application.*;
import org.eclipse.rap.rwt.osgi.ApplicationReference;
import org.eclipse.rap.rwt.testfixture.internal.FileUtil;
import org.eclipse.rap.rwt.testfixture.internal.Fixture;
import org.eclipse.rap.service.http.HttpContext;
import org.eclipse.rap.service.http.HttpService;
import org.junit.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.*;
import org.osgi.service.log.LogService;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;


public class ApplicationLauncherImpl_Test {

  private static final String CONTEXT_NAME = "context";
  private static final String FILTER_EXPRESSION = "(key=value)";
  private static final String SERVLET_PATH_1 = "/servlet1";
  private static final String SERVLET_PATH_2 = "/servlet2";

  private BundleContext bundleContext;
  private HttpService httpService;
  private ServiceReference<HttpService> httpServiceReference;
  private ApplicationConfiguration configuration;
  private ServiceReference<ApplicationConfiguration> configuratorReference;
  private ApplicationLauncherImpl applicationLauncher;
  private ServiceRegistration<?> serviceRegistration;
  private LogService log;

  @Before
  public void setUp() {
    Fixture.deleteWebContextDirectory();
    Fixture.setSkipResourceDeletion( false );
    mockConfigurator();
    mockHttpService();
    mockBundleContext();
    createApplicationLauncher();
  }

  @After
  public void tearDown() {
    FileUtil.delete( Fixture.WEB_CONTEXT_DIR );
    Fixture.resetSkipResourceDeletion();
  }

  @Test
  public void testLaunch() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();

    applicationLauncher.launch( configuration, httpService, null, null, path );

    checkDefaultAliasHasBeenRegistered();
    checkHttpContextHasBeenCreated();
    checkApplicationReferenceHasBeenRegisteredAsService();
  }

  @Test
  public void testLaunchWithHttpContext() {
    HttpContext httpContext = mock( HttpContext.class );
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    applicationLauncher.launch( configuration, httpService, httpContext, null, path );

    checkDefaultAliasHasBeenRegistered();
    checkHttpContextHasBeenWrapped();
    checkApplicationReferenceHasBeenRegisteredAsService();
  }

  @Test
  public void testLaunchWithDefaultContextDirectory() {
    launchApplication();

    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testLaunchWithProblem() {
    prepareConfiguratorToThrowException();
    mockLogService();

    registerServiceReferences();

    checkProblemHasBeenLogged();
  }

  @Test
  public void testStopApplication() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    ApplicationReference context = launchApplicationReference( path );

    context.stopApplication();

    checkDefaultAliasHasBeenUnregistered();
    checkApplicationReferenceHasBeenUnregisteredAsService();
  }

  @Test
  public void testStopApplicationReferenceWithProblem() {
    mockLogService();
    ApplicationReferenceImpl applicationReference = createMalignApplicationReference();

    applicationLauncher.stopApplicationReference( applicationReference );

    checkProblemHasBeenLogged();
  }

  @Test
  public void testActivationStateAfterDeactivation() {
    ApplicationReference applicationReference = launchApplication();

    applicationLauncher.deactivate();

    checkDeactivateStateOfApplicationReference( applicationReference );
    checkDeactivatedStateOfApplicationLauncher();
    checkDefaultAliasHasBeenUnregistered();
  }

  @Test
  public void testLaunchWithMultipleServletNames() {
    createAliasConfigurator( SERVLET_PATH_1, SERVLET_PATH_2 );
    createApplicationLauncher();

    launchApplication();

    checkAliasHasBeenRegistered( SERVLET_PATH_1 );
    checkAliasHasBeenRegistered( SERVLET_PATH_2 );
  }

  @Test
  public void testStopApplicationWithMultipleServletNames() {
    createAliasConfigurator( SERVLET_PATH_1, SERVLET_PATH_2 );
    createApplicationLauncher();
    ApplicationReference applicationReference = launchApplication();

    applicationReference.stopApplication();

    checkAliasHasBeenUnregistered( SERVLET_PATH_1 );
    checkAliasHasBeenUnregistered( SERVLET_PATH_2 );
  }

  @Test
  public void testLaunchWithContextName() {
    mockBundleContext( CONTEXT_NAME );
    createApplicationLauncher();
    String location = applicationLauncher.getLocation( CONTEXT_NAME, configuration, httpService );

    applicationLauncher.launch( configuration, httpService, null, CONTEXT_NAME, location );

    checkAliasHasBeenRegistered( "/" + CONTEXT_NAME + ApplicationReferenceImpl.DEFAULT_ALIAS );
  }

  @Test
  public void testStopApplicationWithContextName() {
    mockBundleContext( CONTEXT_NAME );
    createApplicationLauncher();
    String location = applicationLauncher.getLocation( CONTEXT_NAME, configuration, httpService );
    ApplicationReference applicationReference
      = applicationLauncher.launch( configuration, httpService, null, CONTEXT_NAME, location );

    applicationReference.stopApplication();

    checkAliasHasBeenUnregistered( "/" + CONTEXT_NAME + ApplicationReferenceImpl.DEFAULT_ALIAS );
  }

  @Test
  public void testActivate() {
    registerServiceReferences();

    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testDeactivate() {
    ApplicationReferenceImpl applicationReference = ( ApplicationReferenceImpl )launchApplication();

    applicationLauncher.deactivate();

    assertFalse( applicationReference.isAlive() );
  }

  @Test
  public void testAddConfigurator() {
    applicationLauncher.addHttpService( httpServiceReference );

    ApplicationConfiguration added = applicationLauncher.addConfiguration( configuratorReference );

    assertSame( configuration, added );
    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testRemoveConfigurator() {
    applicationLauncher.addHttpService( httpServiceReference );
    applicationLauncher.addConfiguration( configuratorReference );

    applicationLauncher.removeConfiguration( configuration );

    checkDefaultAliasHasBeenUnregistered();
  }

  @Test
  public void testAddHttpService() {
    applicationLauncher.addConfiguration( configuratorReference );

    HttpService added = applicationLauncher.addHttpService( httpServiceReference );

    assertSame( httpService, added );
    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testRemoveHttpService() {
    ApplicationReferenceImpl reference1 = ( ApplicationReferenceImpl )launchApplication();
    ApplicationReferenceImpl reference2 = ( ApplicationReferenceImpl )launchApplication();

    applicationLauncher.removeHttpService( httpService );

    assertFalse( reference1.isAlive() );
    assertFalse( reference2.isAlive() );
  }

  @Test
  public void testAddConfigurerAfterLaunch() {
    ApplicationReference reference = launchApplication();
    applicationLauncher.addHttpService( httpServiceReference );
    reference.stopApplication();

    mockSecondConfiguratorReference();

    checkDefaultAliasHasBeenRegisteredTwice();
  }

  @Test
  public void testNonMatchingFilterUsageHttpService() {
    configureHttpServiceFilter( "wrongValue" );

    registerServiceReferences();

    checkDefaultAliasHasNotBeenRegistered();
  }

  @Test
  public void testNonMatchingFilterUsageConfigurator() {
    configureConfiguratorFilter( "wrongValue" );

    registerServiceReferences();

    checkDefaultAliasHasNotBeenRegistered();
  }

  @Test
  public void testMatchingFilterUsageHttpService() {
    configureHttpServiceFilter( "value" );

    registerServiceReferences();

    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testMatchingFilterUsageConfigurator() {
    configureConfiguratorFilter( "value" );

    registerServiceReferences();

    checkDefaultAliasHasBeenRegistered();
  }

  @Test
  public void testContextFileNameIsRelative() {
    // See bug 378778
    String name = ApplicationLauncherImpl.getContextFileName( "contextName",
                                                              configuration,
                                                              httpService );

    assertFalse( new File( name ).isAbsolute() );
  }

  private ServiceRegistration<?> checkApplicationReferenceHasBeenRegisteredAsService() {
    return verify( bundleContext ).registerService( eq( ApplicationReference.class.getName() ),
                                                    any( ApplicationReference.class ),
                                                    isNull() );
  }

  private void checkApplicationReferenceHasBeenUnregisteredAsService() {
    verify( serviceRegistration ).unregister();
  }

  private void checkDefaultAliasHasBeenRegisteredTwice() {
    checkAliasHasBeenRegistered( ApplicationReferenceImpl.DEFAULT_ALIAS, 2 );
  }

  private void checkDefaultAliasHasNotBeenRegistered() {
    checkAliasHasBeenRegistered( ApplicationReferenceImpl.DEFAULT_ALIAS, 0 );
  }

  private void checkDefaultAliasHasBeenRegistered() {
    checkAliasHasBeenRegistered( ApplicationReferenceImpl.DEFAULT_ALIAS, 1 );
  }

  private void checkAliasHasBeenRegistered( String alias ) {
    checkAliasHasBeenRegistered( alias, 1 );
  }

  private void checkAliasHasBeenRegistered( String alias, int times ) {
    try {
      verify( httpService, times( times ) ).registerServlet( eq( alias ),
                                                             any( HttpServlet.class ),
                                                             isNull(),
                                                             any( HttpContext.class ) );
      verify( httpService, times( times ) ).registerResources( eq( getResourcesDirectory( alias ) ),
                                                               any( String.class ),
                                                               any( HttpContext.class ) );
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private static String getResourcesDirectory( String alias ) {
    String result = "/" + ApplicationRunner.RESOURCES;
    if( alias.lastIndexOf( '/' ) > 0 ) {
      result = "/" + CONTEXT_NAME + "/" + ApplicationRunner.RESOURCES;
    }
    return result;
  }

  private void checkDefaultAliasHasBeenUnregistered() {
    checkAliasHasBeenUnregistered( ApplicationReferenceImpl.DEFAULT_ALIAS );
  }

  private void checkAliasHasBeenUnregistered( String alias ) {
    verify( httpService ).unregister( alias );
    verify( httpService ).unregister( getResourcesDirectory( alias ) );
  }

  private static void checkDeactivateStateOfApplicationReference( ApplicationReference reference ) {
    assertFalse( ( ( ApplicationReferenceImpl )reference ).isAlive() );
    reference.stopApplication(); // check that repeatedly calls to stop do not cause any problems
  }

  private void checkDeactivatedStateOfApplicationLauncher() {
    assertFalse( applicationLauncher.isAlive() );
    assertNull( applicationLauncher.launch( configuration, httpService, null, null, "/contextPath" ) );
  }

  private HttpContext checkHttpContextHasBeenWrapped() {
    return verify( httpService, never() ).createDefaultHttpContext();
  }

  private void checkHttpContextHasBeenCreated() {
    verify( httpService ).createDefaultHttpContext();
  }

  private void checkProblemHasBeenLogged() {
    verify( log ).log( eq( LogService.LOG_ERROR ),
                       any( String.class ),
                       any( IllegalStateException.class ) );
  }

  private void mockLogService() {
    log = mock( LogService.class );
    ServiceReference<?> logReference = mock( ServiceReference.class );
    doReturn( logReference ).when( bundleContext ).getServiceReference( LogService.class.getName() );
    doReturn( log ).when( bundleContext ).getService( logReference );
  }

  private void registerServiceReferences() {
    applicationLauncher.addConfiguration( configuratorReference );
    applicationLauncher.addHttpService( httpServiceReference );
  }

  private void configureConfiguratorFilter( String value ) {
    Class<?> targetType = HttpService.class;
    ServiceReference<?> serviceReference = configuratorReference;
    ServiceReference<?> targetReference = httpServiceReference;
    configureFilterScenario( value, targetType, serviceReference, targetReference );
  }

  private void configureHttpServiceFilter( String value ) {
    Class<?> targetType = ApplicationConfiguration.class;
    ServiceReference<?> serviceReference = httpServiceReference;
    ServiceReference<?> targetReference = configuratorReference;
    configureFilterScenario( value, targetType, serviceReference, targetReference );
  }

  private static void configureFilterScenario( String value,
                                        Class<?> targetType,
                                        ServiceReference<?> serviceReference,
                                        ServiceReference<?> targetReference )
  {
    String target = Matcher.createTargetKey( targetType );
    when( serviceReference.getProperty( target ) ).thenReturn( FILTER_EXPRESSION );
    when( targetReference.getProperty( "key" ) ).thenReturn( value );
  }

  private void createApplicationLauncher() {
    applicationLauncher = new ApplicationLauncherImpl( bundleContext );
  }

  private void mockHttpServiceReference() {
    httpServiceReference =  mock( ServiceReference.class );
  }

  private void mockConfigurator() {
    configuration = mock( ApplicationConfiguration.class );
    mockConfiguratorReference();
  }

  private void mockConfiguratorReference() {
    configuratorReference = mock( ServiceReference.class );
  }

  private void mockSecondConfiguratorReference() {
    mockConfiguratorReference();
    configuration = mock( ApplicationConfiguration.class );
    when( bundleContext.getService( configuratorReference ) ).thenReturn( configuration );
    when( bundleContext.getDataFile( any( String.class ) ) ).thenReturn( Fixture.WEB_CONTEXT_DIR );
    applicationLauncher.addConfiguration( configuratorReference );
  }


  private void createAliasConfigurator( final String servletPath1, final String servletPath2 ) {
    configuration = new ApplicationConfiguration() {
      @Override
      public void configure( Application configuration ) {
        configuration.addEntryPoint( servletPath1, TestEntryPoint.class, null );
        configuration.addEntryPoint( servletPath2, TestEntryPoint.class, null );
      }
    };
    mockBundleContext();
  }

  private void mockHttpService() {
    httpService = mock( HttpService.class );
    mockServletConfigForServletContextRetrieval( httpService );
    mockHttpServiceReference();
  }

  private static void mockServletConfigForServletContextRetrieval( HttpService service ) {
    String servletContextFinderAlias = ApplicationReferenceImpl.SERVLET_CONTEXT_FINDER_ALIAS;
    String alias1 = servletContextFinderAlias;
    String alias2 = "/" + CONTEXT_NAME + servletContextFinderAlias;
    mockServletConfigForServletContextRetrieval( service, alias1 );
    mockServletConfigForServletContextRetrieval( service, alias2 );
  }

  private static void mockServletConfigForServletContextRetrieval( HttpService service,
                                                                   String alias )
  {
    try {
      doAnswer( mockServletConfigForServletContextRetrieval() )
       .when( service ).registerServlet( eq( alias ),
                                        any( HttpServlet.class ),
                                        isNull(),
                                        any( HttpContext.class ) );
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private static Answer<Object> mockServletConfigForServletContextRetrieval() {
    return new Answer<Object>() {
      @Override
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        HttpServlet servlet = ( HttpServlet )invocation.getArguments()[ 1 ];
        mockServletConfigForServletContextRetrieval( servlet );
        return null;
      }
    };
  }

  private static void mockServletConfigForServletContextRetrieval( HttpServlet servlet ) {
    ServletConfig servletConfig = mock( ServletConfig.class );
    // use the fixture servlet context for performanc optimizations
    ServletContext servletContext = Fixture.createServletContext();
    when( servletConfig.getServletContext() ).thenReturn( servletContext );
    initServlet( servlet, servletConfig );
  }

  private static void initServlet( HttpServlet servlet, ServletConfig servletConfig ) {
    try {
      servlet.init( servletConfig );
    } catch( ServletException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void mockBundleContext() {
    mockBundleContext( null );
  }

  private void mockBundleContext( String contextName ) {
    bundleContext = mock( BundleContext.class );
    String name = ApplicationLauncherImpl.getContextFileName( contextName, configuration, httpService );
    when( bundleContext.getDataFile( eq( name ) ) ).thenReturn( Fixture.WEB_CONTEXT_DIR );
    when( bundleContext.getService( httpServiceReference ) ).thenReturn( httpService );
    when( bundleContext.getService( configuratorReference ) ).thenReturn( configuration );
    serviceRegistration = mock( ServiceRegistration.class );
    doReturn( serviceRegistration )
      .when( bundleContext ).registerService( eq( ApplicationReference.class.getName() ),
                                              any( ApplicationReference.class ),
                                              isNull() );
  }

  private ApplicationReference launchApplication() {
    String location = applicationLauncher.getLocation( null, configuration, httpService );
    return launchApplicationReference( location );
  }

  private ApplicationReference launchApplicationReference( String location ) {
    return applicationLauncher.launch( configuration, httpService, null, null, location );
  }

  private void prepareConfiguratorToThrowException() {
    doThrow( new IllegalStateException() )
      .when( configuration ).configure( any( Application.class ) );
  }

  private static ApplicationReferenceImpl createMalignApplicationReference() {
    ApplicationReferenceImpl result = mock( ApplicationReferenceImpl.class );
    doThrow( new IllegalStateException() ).when( result ).stopApplication();
    return result;
  }

  private static class TestEntryPoint implements EntryPoint {

    @Override
    public int createUI() {
      return 0;
    }

  }

}
