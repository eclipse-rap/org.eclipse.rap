package org.eclipse.rap.rwt.cluster.testfixture.internal.tomcat;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.catalina.*;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.eclipse.rap.rwt.cluster.testfixture.internal.server.DelegatingServletEngine;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.FileUtil;
import org.eclipse.rap.rwt.cluster.testfixture.internal.util.SocketUtil;
import org.eclipse.rap.rwt.cluster.testfixture.server.IServletEngine;
import org.eclipse.rwt.internal.engine.*;
import org.eclipse.rwt.lifecycle.IEntryPoint;


@SuppressWarnings("restriction")
public class TomcatEngine implements IServletEngine {

  private final int port;
  private final Tomcat tomcat;
  private final Context context;

  public TomcatEngine() {
    this( SocketUtil.getFreePort() ); 
  }
  
  TomcatEngine( int port ) {
    this.port = port;
    this.tomcat = new Tomcat();
    configureTomcat(); 
    this.context = tomcat.addContext( "/", tomcat.getHost().getAppBase() );
  }
  
  private void configureTomcat() {
    tomcat.setSilent( true );
    tomcat.setPort( port );
    tomcat.setBaseDir( getBaseDir().getAbsolutePath() );
    tomcat.getHost().setAppBase( getWebAppsDir().getAbsolutePath() );
    // Seems that this must be unique among all embedded Tomcats
    tomcat.getEngine().setName( "Tomcat on port " + port );
  }
  
  public void start( Class<? extends IEntryPoint> entryPointClass ) throws Exception {
    prepareWebAppsDir();
    configureContext( entryPointClass );
    tomcat.start();
  }

  public void stop() throws Exception {
    tomcat.getEngine().setCluster( null );
    tomcat.stop();
    FileUtil.deleteDirectory( getBaseDir() );
  }
  
  public int getPort() {
    return port;
  }
  
  public HttpURLConnection createConnection( URL url ) throws IOException {
    return ( HttpURLConnection )url.openConnection();
  }

  public HttpSession[] getSessions() {
    Session[] sessions = context.getManager().findSessions();
    HttpSession[] result = new HttpSession[ sessions.length ];
    for( int i = 0; i < sessions.length; i++ ) {
      result[ i ] = sessions[ i ].getSession();
    }
    return result;
  }
  
  Engine getEngine() {
    return tomcat.getEngine();
  }

  private boolean prepareWebAppsDir() {
    return new File( tomcat.getHost().getAppBase() ).mkdirs();
  }

  private void configureContext( Class<? extends IEntryPoint> entryPointClass ) {
    context.setDistributable( true );
    context.setSessionTimeout( -1 );
    context.addParameter( "org.eclipse.rwt.entryPoints", entryPointClass.getName() );
    context.addApplicationListener( RWTServletContextListener.class.getName() );
    Wrapper rwtServlet = addServlet( "rwtServlet", new RWTDelegate() );
    context.addServletMapping( "/rap", rwtServlet.getName() );
    Wrapper defaultServlet = addServlet( "defaultServlet", new DefaultServlet() );
    context.addServletMapping( "/", defaultServlet.getName() );
    addFilter( rwtServlet, new RWTClusterSupport() );
  }

  private Wrapper addServlet( String name, HttpServlet servlet ) {
    return Tomcat.addServlet( context, name, servlet );
  }

  private void addFilter( Wrapper rwtServlet, Filter filter ) {
    FilterDef filterDef = new FilterDef();
    filterDef.setFilter( filter );
    filterDef.setFilterName( "rwtClusterSupport" );
    context.addFilterDef( filterDef );
    FilterMap filterMap = new FilterMap();
    filterMap.addServletName( rwtServlet.getName() );
    filterMap.setFilterName( filterDef.getFilterName() );
    context.addFilterMap( filterMap );
  }

  private File getBaseDir() {
    return DelegatingServletEngine.getTempDir( this );
  }

  private File getWebAppsDir() {
    return new File( getBaseDir(), "webapps" );
  }
}
