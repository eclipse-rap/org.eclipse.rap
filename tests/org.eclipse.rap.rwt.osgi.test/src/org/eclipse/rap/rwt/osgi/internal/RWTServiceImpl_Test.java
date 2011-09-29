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
package org.eclipse.rap.rwt.osgi.internal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Dictionary;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;

import junit.framework.TestCase;

import org.eclipse.rap.rwt.osgi.RWTContext;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rwt.branding.AbstractBranding;
import org.eclipse.rwt.engine.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.*;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.log.LogService;


public class RWTServiceImpl_Test extends TestCase {
  private static final String CONTEXT_NAME = "context";
  private static final String FILTER_EXPRESSION = "(key=value)";
  private static final String SERVLET_ALIAS_1 = "servlet1";
  private static final String SERVLET_ALIAS_2 = "servlet2";

  private BundleContext bundleContext;
  private HttpService httpService;
  private ServiceReference< HttpService > httpServiceReference;
  private Configurator configurator;
  private ServiceReference< Configurator > configuratorReference;
  private RWTServiceImpl service;
  private ServiceRegistration serviceRegistration;
  private LogService log;
  
  public void testStart() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    
    service.start( configurator, httpService, null, null, path );
    
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
    checkHttpContextHasBeenCreated();
    checkRWTContextHasBeenRegisteredAsService();
  }

  public void testStartWithHttpContext() {
    HttpContext httpContext = mock( HttpContext.class );
    service.start( configurator, httpService, httpContext, null, Fixture.WEB_CONTEXT_DIR.getPath() );
    
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
    checkHttpContextHasBeenWrapped();
    checkRWTContextHasBeenRegisteredAsService();
  }
  
  public void testStartWithDefaultContextDirectory() {
    startRWTContext();
    
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
  }
  
  public void testStartWithProblem() {
    prepareConfiguratorToThrowException();
    mockLogService();
    
    registerServiceReferences();
    
    checkProblemHasBeenLogged();
  }

  public void testStop() {
    String path = Fixture.WEB_CONTEXT_DIR.getPath();
    RWTContext context = startRWTContext( path );
    
    context.stop();

    checkDefaultAliasHasBeenUnregistered();
    checkWebContextResourcesHaveBeenDeleted();
    checkRWTContextHasBeenUnregisteredAsService();
  }
  
  public void testStopWithProblem() {
    mockLogService();
    RWTContextImpl rwtContext = createMalignRWTContext();
    
    service.stopContext( rwtContext );
    
    checkProblemHasBeenLogged();
  }
  
  public void testActivationStateAfterDeactivation() {
    RWTContext context = startRWTContext();

    service.deactivate();

    checkDeactivateStateOfRWTContext( context );
    checkDeactivatedStateOfRWTService();
    checkDefaultAliasHasBeenUnregistered();
    checkWebContextResourcesHaveBeenDeleted();
  }
  
  public void testStartWithMultipleServletNames() {
    createAliasConfigurator( SERVLET_ALIAS_1, SERVLET_ALIAS_2 );
    createService();
    
    startRWTContext();
    
    checkAliasHasBeenRegistered( SERVLET_ALIAS_1 );
    checkAliasHasBeenRegistered( SERVLET_ALIAS_2 );
  }
  
  public void testStopWithMultipleServletNames() {
    createAliasConfigurator( SERVLET_ALIAS_1, SERVLET_ALIAS_2 );
    createService();
    RWTContext context = startRWTContext();

    context.stop();
    
    checkAliasHasBeenUnregistered( SERVLET_ALIAS_1 );
    checkAliasHasBeenUnregistered( SERVLET_ALIAS_2 );
  }
  
  public void testStartWithContextName() {
    mockBundleContext( CONTEXT_NAME );
    createService();
    String location = service.getLocation( CONTEXT_NAME, configurator, httpService );
    
    service.start( configurator, httpService, null, CONTEXT_NAME, location );
    
    checkAliasHasBeenRegistered( CONTEXT_NAME + "/" + RWTContextImpl.DEFAULT_ALIAS );
  }
  
  public void testStopWithContextName() {
    mockBundleContext( CONTEXT_NAME );
    createService();
    String location = service.getLocation( CONTEXT_NAME, configurator, httpService );
    RWTContext context = service.start( configurator, httpService, null, CONTEXT_NAME, location );
    
    context.stop();
    
    checkAliasHasBeenUnregistered( CONTEXT_NAME + "/" + RWTContextImpl.DEFAULT_ALIAS );
  }
  
  public void testActivate() {
    registerServiceReferences();
    
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
  }
  
  public void testDeactivate() {
    RWTContextImpl context = ( RWTContextImpl )startRWTContext();
    
    service.deactivate();
    
    assertFalse( context.isAlive() );
  }
  
  public void testAddConfigurator() {
    service.addHttpService( httpServiceReference );
    
    Configurator added = service.addConfigurator( configuratorReference );

    assertSame( configurator, added );
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
  }
  
  public void testRemoveConfigurator() {
    service.addHttpService( httpServiceReference );
    service.addConfigurator( configuratorReference );

    service.removeConfigurator( configurator );
    
    checkDefaultAliasHasBeenUnregistered();
    checkWebContextResourcesHaveBeenDeleted();
  }
  
  public void testAddHttpService() {
    service.addConfigurator( configuratorReference );
    
    HttpService added = service.addHttpService( httpServiceReference );
    
    assertSame( httpService, added );
    checkDefaultAliasHasBeenRegistered();
    checkWebContextResourcesHaveBeenCreated();
  }
  
  public void testRemoveHttpService() {
    RWTContextImpl context1 = ( RWTContextImpl )startRWTContext();
    RWTContextImpl context2 = ( RWTContextImpl )startRWTContext();
    
    service.removeHttpService( httpService );
    
    assertFalse( context1.isAlive() );
    assertFalse( context2.isAlive() );
    checkWebContextResourcesHaveBeenDeleted();
  }
  
  public void testAddConfigurerAfterStart() {
    RWTContext context = startRWTContext();
    service.addHttpService( httpServiceReference );
    context.stop();
    
    mockSecondConfiguratorReference();
    
    checkDefaultAliasHasBeenRegisteredTwice();
  }
  
  public void testNonMatchingFilterUsageHttpService() {
    configureHttpServiceFilter( "wrongValue" );
    
    registerServiceReferences();
    
    checkDefaultAliasHasNotBeenRegistered();
  }

  public void testNonMatchingFilterUsageConfigurator() {
    configureConfiguratorFilter( "wrongValue" );
    
    registerServiceReferences();
    
    checkDefaultAliasHasNotBeenRegistered();
  }
  
  public void testMatchingFilterUsageHttpService() {
    configureHttpServiceFilter( "value" );
    
    registerServiceReferences();
    
    checkDefaultAliasHasBeenRegistered();
  }
  
  public void testMatchingFilterUsageConfigurator() {
    configureConfiguratorFilter( "value" );

    registerServiceReferences();
    
    checkDefaultAliasHasBeenRegistered();
  }

  protected void setUp() {
    Fixture.deleteWebContextDirectories();
    Fixture.setIgnoreResourceDeletion( false );
    Fixture.useTestResourceManager();
    mockConfigurator();
    mockHttpService();
    mockBundleContext();
    createService();
  }

  protected void tearDown() {
    Fixture.delete( Fixture.WEB_CONTEXT_DIR );
    Fixture.setIgnoreResourceDeletion( Fixture.usePerformanceOptimizations() );
  }

  @SuppressWarnings( "unchecked" )
  private ServiceRegistration< ? > checkRWTContextHasBeenRegisteredAsService() {
    return verify( bundleContext ).registerService( eq( RWTContext.class.getName() ),
                                             any( RWTContext.class ),
                                             any( Dictionary.class ) );
  }

  private void checkRWTContextHasBeenUnregisteredAsService() {
    verify( serviceRegistration ).unregister();
  }

  private void checkDefaultAliasHasBeenRegisteredTwice() {
    checkAliasHasBeenRegistered( RWTContextImpl.DEFAULT_ALIAS, 2 );
  }

  private void checkDefaultAliasHasNotBeenRegistered() {
    checkAliasHasBeenRegistered( RWTContextImpl.DEFAULT_ALIAS, 0 );
  }
  
  private void checkDefaultAliasHasBeenRegistered() {
    checkAliasHasBeenRegistered( RWTContextImpl.DEFAULT_ALIAS, 1 );
  }

  private void checkAliasHasBeenRegistered( String alias ) {
    checkAliasHasBeenRegistered( alias, 1 );
  }
  
  private void checkAliasHasBeenRegistered( String alias, int times )  {
    try {
      verify( httpService, times( times ) ).registerServlet( eq( "/" + alias ),
                                                             any( HttpServlet.class ), 
                                                             any( Dictionary.class ), 
                                                             any( HttpContext.class ) );
      verify( httpService, times( times ) ).registerResources( eq( getResourcesDirectory( alias ) ),
                                                               any( String.class ), 
                                                               any( HttpContext.class ) );
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private String getResourcesDirectory( String alias ) {
    String result = "/" + ContextControl.RESOURCES;
    if( alias.contains( "/" ) ) {
      result = "/" + CONTEXT_NAME + "/" + ContextControl.RESOURCES;
    }
    return result;
  }
  
  private void checkDefaultAliasHasBeenUnregistered() {
    checkAliasHasBeenUnregistered( RWTContextImpl.DEFAULT_ALIAS );
  }

  private void checkAliasHasBeenUnregistered( String alias ) {
    verify( httpService ).unregister( "/" + alias );
    verify( httpService ).unregister( getResourcesDirectory( alias ) );
  }
  
  private void checkWebContextResourcesHaveBeenCreated() {
    assertTrue( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.exists() );
  }
  
  private void checkWebContextResourcesHaveBeenDeleted() {
    assertFalse( Fixture.WEB_CONTEXT_RWT_RESOURCES_DIR.exists() );
  }

  private void checkDeactivateStateOfRWTContext( RWTContext context ) {
    assertFalse( ( ( RWTContextImpl )context ).isAlive() );
    context.stop(); // check that repeatedly calls to stop do not cause any problems
  }

  private void checkDeactivatedStateOfRWTService() {
    assertFalse( service.isAlive() );
    assertNull( service.start( configurator, httpService, null, null, "/contextPath" ) );
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

  @SuppressWarnings( "unchecked" )
  private void mockLogService() {
    log = mock( LogService.class );
    ServiceReference logReference = mock( ServiceReference.class );
    when( bundleContext.getServiceReference( LogService.class.getName() ) )
      .thenReturn( logReference );
    when( bundleContext.getService( logReference ) ).thenReturn( log );
  }

  private void registerServiceReferences() {
    service.addConfigurator( configuratorReference );
    service.addHttpService( httpServiceReference );
  }
  
  private void configureConfiguratorFilter( String value ) {
    Class< ? > targetType = HttpService.class;
    ServiceReference< ?> serviceReference = configuratorReference;
    ServiceReference< ? > targetReference = httpServiceReference;
    configureFilterScenario( value, targetType, serviceReference, targetReference );
  }

  private void configureHttpServiceFilter( String value ) {
    Class< ? > targetType = Configurator.class;
    ServiceReference< ?> serviceReference = httpServiceReference;
    ServiceReference< ? > targetReference = configuratorReference;
    configureFilterScenario( value, targetType, serviceReference, targetReference );
  }

  private void configureFilterScenario( String value,
                                        Class< ? > targetType,
                                        ServiceReference< ? > serviceReference,
                                        ServiceReference< ? > targetReference )
  {
    String target = Matcher.createTargetKey( targetType );
    when( serviceReference.getProperty( target ) ).thenReturn( FILTER_EXPRESSION );
    when( targetReference.getProperty( "key" ) ).thenReturn( value );
  }

  private void createService() {
    service = new RWTServiceImpl( bundleContext );
  }

  @SuppressWarnings( "unchecked" )
  private void mockHttpServiceReference() {
    httpServiceReference =  mock( ServiceReference.class );
  }

  private void mockConfigurator() {
    configurator = mock( Configurator.class );
    mockConfiguratorReference();
  }

  @SuppressWarnings( "unchecked" )
  private void mockConfiguratorReference() {
    configuratorReference = mock( ServiceReference.class );
  }

  private void mockSecondConfiguratorReference() {
    mockConfiguratorReference();
    configurator = mock( Configurator.class );
    when( bundleContext.getService( configuratorReference ) ).thenReturn( configurator );
    when( bundleContext.getDataFile( any( String.class ) ) ).thenReturn( Fixture.WEB_CONTEXT_DIR );
    service.addConfigurator( configuratorReference );
  }

  
  private void createAliasConfigurator( final String alias1, final String alias2 ) {
    configurator = new Configurator() {
      public void configure( Context application ) {
        application.addBranding( mockBranding( alias1 ) );
        application.addBranding( mockBranding( alias2 ) );
      }
    };
    mockBundleContext();
  }

  private void mockHttpService() {
    httpService = mock( HttpService.class );
    mockServletConfigForServletContextRetrieval( httpService );
    mockHttpServiceReference();
  }

  private void mockServletConfigForServletContextRetrieval( HttpService service ) {
    String alias1 = "/" + RWTContextImpl.SERVLET_CONTEXT_FINDER_ALIAS;
    String alias2 = "/" + CONTEXT_NAME + "/" + RWTContextImpl.SERVLET_CONTEXT_FINDER_ALIAS;
    mockServletConfigForServletContextRetrieval( service, alias1 );
    mockServletConfigForServletContextRetrieval( service, alias2 );
  }

  private void mockServletConfigForServletContextRetrieval( HttpService service, String alias ) {
    try {
      doAnswer( mockServletConfigForServletContextRetrieval() )
       .when( service ).registerServlet( eq( alias ),
                                        any( HttpServlet.class ),
                                        any( Dictionary.class ),
                                        any( HttpContext.class ) );
    } catch( Exception shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private Answer mockServletConfigForServletContextRetrieval() {
    return new Answer() {
      public Object answer( InvocationOnMock invocation ) throws Throwable {
        HttpServlet servlet = ( HttpServlet )invocation.getArguments()[ 1 ];
        mockServletConfigForServletContextRetrieval( servlet );
        return null;
      }
    };
  }

  private void mockServletConfigForServletContextRetrieval( HttpServlet servlet ) {
    ServletConfig servletConfig = mock( ServletConfig.class );
    // use the fixture servlet context for performanc optimizations
    ServletContext servletContext = Fixture.createServletContext();
    when( servletConfig.getServletContext() ).thenReturn( servletContext );
    initServlet( servlet, servletConfig );
  }

  private void initServlet( HttpServlet servlet, ServletConfig servletConfig ) {
    try {
      servlet.init( servletConfig );
    } catch( ServletException shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
  }

  private void mockBundleContext() {
    mockBundleContext( null );
  }

  @SuppressWarnings( "unchecked" )
  private void mockBundleContext( String contextName ) {
    bundleContext = mock( BundleContext.class );
    String name = RWTServiceImpl.getContextFileName( contextName, configurator, httpService );
    when( bundleContext.getDataFile( eq( name ) ) ).thenReturn( Fixture.WEB_CONTEXT_DIR );
    when( bundleContext.getService( httpServiceReference ) ).thenReturn( httpService );
    when( bundleContext.getService( configuratorReference ) ).thenReturn( configurator );
    serviceRegistration = mock( ServiceRegistration.class );
    when( bundleContext.registerService( eq( RWTContext.class.getName() ), 
                                         any( RWTContext.class ),
                                         any( Dictionary.class ) ) )
      .thenReturn( serviceRegistration );
  }

  private AbstractBranding mockBranding( String servletName ) {
    AbstractBranding result = mock( AbstractBranding.class );
    when( result.getServletName() ).thenReturn( servletName );
    return result;
  }
  
  private RWTContext startRWTContext() {
    String location = service.getLocation( null, configurator, httpService );
    return startRWTContext( location );
  }

  private RWTContext startRWTContext( String location ) {
    return service.start( configurator, httpService, null, null, location );
  }
  
  private void prepareConfiguratorToThrowException() {
    doThrow( new IllegalStateException() ).when( configurator ).configure( any( Context.class ) );
  }

  private RWTContextImpl createMalignRWTContext() {
    RWTContextImpl result = mock( RWTContextImpl.class );
    doThrow( new IllegalStateException() ).when( result ).stop();
    return result;
  }
}