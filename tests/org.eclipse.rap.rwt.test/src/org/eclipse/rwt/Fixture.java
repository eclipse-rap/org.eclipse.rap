/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.eclipse.rwt.internal.*;
import org.eclipse.rwt.internal.browser.Browser;
import org.eclipse.rwt.internal.lifecycle.*;
import org.eclipse.rwt.internal.resources.*;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.PhaseId;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.ISessionStore;
import org.xml.sax.SAXException;

/*
 * This class provides fake context data for test runs.
 */
public class Fixture {

  public final static File TEMP_DIR 
    = new File( System.getProperty( "java.io.tmpdir" ) );
  public final static File CONTEXT_DIR = new File( TEMP_DIR, "testapp" );
  public final static String OPERATING_SYSTEM 
    = System.getProperty( "os.name" );
  
  public final static class TestResourceManager
    implements IResourceManager, Adaptable
  {
    
    public Object getAdapter( final Class adapter ) {
      return new JsConcatenator() {
        public void startJsConcatenation() {
        }
        public String getContent() {
          return "";
        }
        public String getLocation() {
          return "";
        }
      };
    }

    public String getCharset( final String name ) {
      return null;
    }

    public ClassLoader getContextLoader() {
      return null;
    }

    public String getLocation( final String name ) {
      return null;
    }

    public URL getResource( final String name ) {
      return null;
    }

    public InputStream getResourceAsStream( final String name ) {
      return null;
    }

    public Enumeration getResources( final String name ) throws IOException {
      return null;
    }

    public boolean isRegistered( final String name ) {
      return false;
    }

    public void register( final String name ) {
      
    }
    
    public void register( final String name, final InputStream is ) {
      
    }

    public void register( final String name, final String charset ) {
      
    }

    public void register( final String name, 
                          final String charset, 
                          final RegisterOptions options )
    {
      
    }

    public void register( String name,
                          InputStream is,
                          String charset,
                          RegisterOptions options )
    {
      
    }

    public void setContextLoader( final ClassLoader classLoader ) {
      
    }
    
  }

  public final static class TestRequest implements HttpServletRequest {
    
    private HttpSession session;
    private String scheme = "http";
    private String serverName = "fooserver";
    private String contextPath = "/fooapp";
    private String requestURI = "/fooapp/W4TDelegate";
    private final StringBuffer requestURL = new StringBuffer();
    private String servletPath = "/W4TDelegate";
    private Map parameters = new HashMap();
    private Map headers = new HashMap();
    private Map attributes = new HashMap();
    private Locale locale;
    
    public String getAuthType() {
      return null;
    }
    
    public Cookie[] getCookies() {
      return null;
    }
    
    public long getDateHeader( final String arg0 ) {
      return 0;
    }
    
    public String getHeader( final String arg0 ) {
      return ( String )headers.get( arg0 );
    }
    
    public void setHeader(final String arg0, final String arg1) {
      headers.put(arg0, arg1);      
    }
    
    public Enumeration getHeaders( final String arg0 ) {
      return null;
    }
    
    public Enumeration getHeaderNames() {
      return new Enumeration() {
        private Iterator iterator = headers.keySet().iterator();
        public boolean hasMoreElements() {
          return iterator.hasNext();
        }
        public Object nextElement() {
          return iterator.next();
        }
      };
    }
    
    public int getIntHeader( final String arg0 ) {
      return 0;
    }
    
    public String getMethod() {
      return null;
    }
    
    public String getPathInfo() {
      return null;
    }
    
    public String getPathTranslated() {
      return null;
    }
    
    public String getContextPath() {
      return contextPath;
    }
    
    public String getQueryString() {
      return null;
    }
    
    public String getRemoteUser() {
      return null;
    }
    
    public boolean isUserInRole( final String arg0 ) {
      return false;
    }
    
    public Principal getUserPrincipal() {
      return null;
    }
    
    public String getRequestedSessionId() {
      return null;
    }
    
    /**
     * @return  Returns the requestURI.
     * @uml.property  name="requestURI"
     */
    public String getRequestURI() {
      return requestURI;
    }
    
    /**
     * @param requestURI  The requestURI to set.
     * @uml.property  name="requestURI"
     */
    public void setRequestURI( final String requestURI ) {
      this.requestURI = requestURI;
    }
    
    /**
     * @return  Returns the requestURL.
     * @uml.property  name="requestURL"
     */
    public StringBuffer getRequestURL() {
      return requestURL;
    }
    
    /**
     * @return  Returns the servletPath.
     * @uml.property  name="servletPath"
     */
    public String getServletPath() {
      return servletPath;
    }
    
    /**
     * @param servletPath  The servletPath to set.
     * @uml.property  name="servletPath"
     */
    public void setServletPath( final String servletPath ) {
      this.servletPath = servletPath;
    }
    
    public HttpSession getSession( final boolean arg0 ) {
      return session;
    }
    
    /**
     * @return  Returns the session.
     * @uml.property  name="session"
     */
    public HttpSession getSession() {
      return session;
    }
    
    public boolean isRequestedSessionIdValid() {
      return false;
    }
    
    public boolean isRequestedSessionIdFromCookie() {
      return false;
    }
    
    public boolean isRequestedSessionIdFromURL() {
      return false;
    }
    
    public boolean isRequestedSessionIdFromUrl() {
      return false;
    }
    
    public Object getAttribute( final String arg0 ) {
      return attributes.get( arg0 );
    }
    
    public Enumeration getAttributeNames() {
      return null;
    }
    
    public String getCharacterEncoding() {
      return null;
    }
    
    public void setCharacterEncoding( final String arg0 )
      throws UnsupportedEncodingException
    {
    }
    
    public int getContentLength() {
      return 0;
    }
    
    public String getContentType() {
      return null;
    }
    
    public ServletInputStream getInputStream() throws IOException {
      return null;
    }
    
    public String getParameter( final String arg0 ) {
      String[] value = ( String[] )parameters.get( arg0 );
      String result = null;
      if( value != null ) {
        result = value[ 0 ];
      }
      return result;
    }
    
    public Enumeration getParameterNames() {
      return new Enumeration() {
        private Iterator iterator = parameters.keySet().iterator();
        public boolean hasMoreElements() {
          return iterator.hasNext();
        }
        
        public Object nextElement() {
          return iterator.next();
        }
      };
    }
    
    public String[] getParameterValues( final String arg0 ) {
      return ( String[] )parameters.get( arg0 );
    }
    
    public void setParameter( final String key, final String value ) {      
      if( value == null ) {
        parameters.remove( key );
      } else {
        parameters.put( key, new String[] { value } );
      }
    }
    
    public void addParameter( final String key, final String value ) {
      if( parameters.containsKey( key ) ) {
        String[] values = ( String[] )parameters.get( key );
        String[] newValues = new String[ values.length + 1 ];
        System.arraycopy( values, 0, newValues, 0, values.length );
        newValues[ values.length ] = value;
        parameters.put( key, newValues );
      } else {
        setParameter( key, value );
      }
    }
    
    public Map getParameterMap() {
      return parameters;
    }
    
    public String getProtocol() {
      return null;
    }
    
    /**
     * @return  Returns the scheme.
     * @uml.property  name="scheme"
     */
    public String getScheme() {
      return scheme;
    }
    
    /**
     * @param scheme  The scheme to set.
     * @uml.property  name="scheme"
     */
    public void setScheme( final String scheme ) {
      this.scheme = scheme;
    }
    
    /**
     * @return  Returns the serverName.
     * @uml.property  name="serverName"
     */
    public String getServerName() {
      return serverName;
    }
    
    /**
     * @param serverName  The serverName to set.
     * @uml.property  name="serverName"
     */
    public void setServerName( final String serverName ) {
      this.serverName = serverName;
    }
    
    public int getServerPort() {
      return 8080;
    }
    
    public BufferedReader getReader() throws IOException {
      return null;
    }
    
    public String getRemoteAddr() {
      return null;
    }
    
    public String getRemoteHost() {
      return null;
    }
    
    public void setAttribute( final String arg0, final Object arg1 ) {
      attributes.put( arg0, arg1 );
    }
    
    public void removeAttribute( final String arg0 ) {
    }
    
    public Locale getLocale() {
      return locale == null ? Locale.getDefault() : locale ;
    }

    public void setLocale( final Locale locale ) {
      this.locale = locale;
    }

    public Enumeration getLocales() {
      return null;
    }
    
    public boolean isSecure() {
      return false;
    }
    
    public RequestDispatcher getRequestDispatcher( final String arg0 ) {
      return null;
    }
    
    public String getRealPath( final String arg0 ) {
      return null;
    }
    
    /**
     * @param session  The session to set.
     * @uml.property  name="session"
     */
    public void setSession( final HttpSession session ) {
      this.session = session;
    }

    public String getLocalAddr() {
      throw new UnsupportedOperationException();
    }

    public String getLocalName() {
      throw new UnsupportedOperationException();
    }

    public int getLocalPort() {
      throw new UnsupportedOperationException();
    }

    public int getRemotePort() {
      throw new UnsupportedOperationException();
    }
  }
  
  public final static class TestResponse implements HttpServletResponse {
    
    private ServletOutputStream outStream;
    private String contentType;

    public void addCookie( final Cookie arg0 ) {
    }
    
    public boolean containsHeader( final String arg0 ) {
      return false;
    }
    
    public String encodeURL( final String arg0 ) {
      return arg0;
    }
    
    public String encodeRedirectURL( final String arg0 ) {
      return arg0;
    }
    
    public String encodeUrl( final String arg0 ) {
      return arg0;
    }
    
    public String encodeRedirectUrl( final String arg0 ) {
      return arg0;
    }
    
    public void sendError( final int arg0, final String arg1 )
    throws IOException
    {
    }
    
    public void sendError( final int arg0 ) throws IOException {
    }
    
    public void sendRedirect( final String arg0 ) throws IOException {
    }
    
    public void setDateHeader( final String arg0, final long arg1 ) {
    }
    
    public void addDateHeader( final String arg0, final long arg1 ) {
    }
    
    public void setHeader( final String arg0, final String arg1 ) {
    }
    
    public void addHeader( final String arg0, final String arg1 ) {
    }
    
    public void setIntHeader( final String arg0, final int arg1 ) {
    }
    
    public void addIntHeader( final String arg0, final int arg1 ) {
    }
    
    public void setStatus( final int arg0 ) {
    }
    
    public void setStatus( final int arg0, final String arg1 ) {
    }
    
    public String getCharacterEncoding() {
      return null;
    }
    
    public ServletOutputStream getOutputStream() throws IOException {
      return outStream;
    }
    
    public void setOutputStream( final ServletOutputStream outStream ) {
      this.outStream = outStream;
    }
    
    public PrintWriter getWriter() throws IOException {
      return new PrintWriter( outStream );
    }
    
    public void setContentLength( final int arg0 ) {
    }
    
    public void setContentType( final String contentType ) {
      this.contentType = contentType;
    }
    
    public String getContentType() {
      return contentType;
    }
    
    public void setBufferSize( final int arg0 ) {
    }
    
    public int getBufferSize() {
      return 0;
    }
    
    public void flushBuffer() throws IOException {
    }
    
    public void resetBuffer() {
    }
    
    public boolean isCommitted() {
      return false;
    }
    
    public void reset() {
    }
    
    public void setLocale( final Locale arg0 ) {
    }
    
    public Locale getLocale() {
      return null;
    }

    public void setCharacterEncoding( String charset ) {
      throw new UnsupportedOperationException();
    }
  }
  
  public final static class TestServletContext implements ServletContext {

    private String servletContextName;
    private final Map initParameters = new HashMap();
    private Map attributes = new HashMap();

    public ServletContext getContext( final String arg0 ) {
      return null;
    }

    public int getMajorVersion() {
      return 0;
    }

    public int getMinorVersion() {
      return 0;
    }

    public String getMimeType( final String arg0 ) {
      return null;
    }

    public Set getResourcePaths( final String arg0 ) {
      return null;
    }

    public URL getResource( final String arg0 ) throws MalformedURLException {
      return null;
    }

    public InputStream getResourceAsStream( final String arg0 ) {
      return null;
    }

    public RequestDispatcher getRequestDispatcher( final String arg0 ) {
      return null;
    }

    public RequestDispatcher getNamedDispatcher( final String arg0 ) {
      return null;
    }

    public Servlet getServlet( final String arg0 ) throws ServletException {
      return null;
    }

    public Enumeration getServlets() {
      return null;
    }

    public Enumeration getServletNames() {
      return null;
    }

    public void log( final String arg0 ) {
    }

    public void log( final Exception arg0, final String arg1 ) {
    }

    public void log( final String arg0, final Throwable arg1 ) {
    }

    public String getRealPath( final String arg0 ) {
      return null;
    }

    public String getServerInfo() {
      return null;
    }

    public String getInitParameter( final String name ) {
      return ( String )initParameters.get( name );
    }
    
    public void setInitParameter( final String name, final String value ) {
      initParameters.put( name, value );
    }

    public Enumeration getInitParameterNames() {
      return null;
    }

    public Object getAttribute( final String arg0 ) {
      return attributes.get( arg0 );
    }

    public Enumeration getAttributeNames() {
      return null;
    }

    public void setAttribute( final String arg0, final Object arg1 ) {
      attributes .put( arg0, arg1 );
    }

    public void removeAttribute( final String arg0 ) {
    }

    public String getServletContextName() {
      return servletContextName;
    }
    
    public void setServletContextName( final String servletContextName ) {
      this.servletContextName = servletContextName;
    }
    
  }
  
  public final static class TestSession implements HttpSession {
    
    private final Map attributes = new HashMap();
    private final ServletContext servletContext = new TestServletContext();
    private boolean isInvalidated;
    private boolean newSession;
    
    public long getCreationTime() {
      return 0;
    }
    
    public String getId() {
      if( isInvalidated ) {
        String text 
          = "Unabled to obtain session id. Session already invalidated.";
        throw new IllegalStateException( text );
      }
      return String.valueOf( hashCode() );
    }
    
    public long getLastAccessedTime() {
      return 0;
    }
    
    public ServletContext getServletContext() {
      return servletContext ;
    }
    
    public void setMaxInactiveInterval( final int arg0 ) {
    }
    
    public int getMaxInactiveInterval() {
      return 0;
    }
    
    public HttpSessionContext getSessionContext() {
      return null;
    }
    
    public Object getAttribute( final String arg0 ) {
      return attributes.get( arg0 );
    }
    
    public Object getValue( final String arg0 ) {
      return null;
    }
    
    public Enumeration getAttributeNames() {
      final Iterator iterator = attributes.keySet().iterator();
      return new Enumeration() {
        public boolean hasMoreElements() {
          return iterator.hasNext();
        }
        public Object nextElement() {
          return iterator.next();
        }
      };
    }
    
    public String[] getValueNames() {
      return null;
    }
    
    public void setAttribute( final String arg0, final Object arg1 ) {
      if( arg1 instanceof HttpSessionBindingListener ) {
        HttpSessionBindingListener listener
          = ( HttpSessionBindingListener )arg1;
        listener.valueBound( new HttpSessionBindingEvent( this, arg0, arg1 ) );
      }
      attributes.put( arg0, arg1 );
    }
    
    public void putValue( final String arg0, final Object arg1 ) {
    }
    
    public void removeAttribute( final String arg0 ) {
      Object removed = attributes.remove( arg0 );
      if( removed instanceof HttpSessionBindingListener ) {
        HttpSessionBindingListener listener
          = ( HttpSessionBindingListener )removed;
        HttpSessionBindingEvent evt
          = new HttpSessionBindingEvent( this, arg0, removed );
        listener.valueUnbound( evt );
      }
    }
    
    public void removeValue( final String arg0 ) {
    }
    
    public void invalidate() {
      Object[] keys = attributes.keySet().toArray();
      for( int i = 0; i < keys.length; i++ ) {
        String key = ( String )keys[ i ];
        Object val = attributes.get( key );
        if( val instanceof HttpSessionBindingListener ) {
          HttpSessionBindingListener lsnr = ( HttpSessionBindingListener )val;
          lsnr.valueUnbound( new HttpSessionBindingEvent( this, key, val ) );
        }
      }
      attributes.clear();
      isInvalidated = true;
    }
    
    public boolean isInvalidated() {
      return isInvalidated;
    }
    
    public boolean isNew() {
      return newSession;
    }

    public void setNew( boolean newSession ) {
      this.newSession = newSession;
    }
  }
  
  public static class TestServletConfig implements ServletConfig {
    
    public String getServletName() {
      return null;
    }
    
    public ServletContext getServletContext() {
      return null;
    }
    
    public String getInitParameter( final String initParameter ) {
      return null;
    }
    
    public Enumeration getInitParameterNames() {
      return null;
    }
    
  }
  
  public static class TestServletOutputStream extends ServletOutputStream {

    private ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    public void write( final int b ) throws IOException {
      stream.write( b );
    }
    
    public ByteArrayOutputStream getContent() {
      return stream;
    }
  }
  
  private Fixture() {
  }
  
  public static void setUp() {
    // disable js-versioning by default to make comparison easier
    System.setProperty( SystemProps.USE_VERSIONED_JAVA_SCRIPT, "false" );
    clearSingletons();
    try {
      ConfigurationReader.setConfigurationFile( null );
    } catch( Throwable shouldNotHappen ) {
      throw new RuntimeException( shouldNotHappen );
    }
    
    TestResponse response = new TestResponse();
    TestRequest request = new TestRequest();
    request.setSession( new TestSession() );
    fakeContextProvider( response, request );
  }

  public static void clearSingletons() {
    setPrivateField( ResourceManagerImpl.class, null, "_instance", null );
    setPrivateField( LifeCycleFactory.class, null, "globalLifeCycle", null );
  }

  public static void tearDown() {
    HttpSession session = ContextProvider.getRequest().getSession();
    ContextProvider.disposeContext();
    session.invalidate();
    clearSingletons();
  }
  
  public static void createContext( final boolean fake )
    throws IOException, 
           FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException
  {
    if( fake ) {
      setPrivateField( ResourceManagerImpl.class,
                       null, 
                       "_instance",
                       new TestResourceManager() );
    } else {
      createContextWithoutResourceManager();
      String webAppBase = CONTEXT_DIR.toString();
      String deliverFromDisk = IInitialization.RESOURCES_DELIVER_FROM_DISK;
      ResourceManagerImpl.createInstance( webAppBase, deliverFromDisk );
    }
  }
  
  public static void createContext()
    throws IOException, 
           FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException
  {
    createContext( true );
  }
  
  public static void createContextWithoutResourceManager()
    throws FileNotFoundException, 
           IOException, 
           FactoryConfigurationError, 
           ParserConfigurationException, 
           SAXException
  {
    CONTEXT_DIR.mkdirs();
    File webInf = new File( CONTEXT_DIR, "WEB-INF" );
    webInf.mkdirs();
    File conf = new File( webInf, "conf" );
    conf.mkdirs();
    File classes = new File( webInf, "classes" );
    classes.mkdirs();
    File libDir = new File( webInf, "lib" );
    libDir.mkdirs();
    File w4tXml = new File( conf, "W4T.xml" );
    copyTestResource( "resources/w4t_fixture.xml", w4tXml );
    
    String webAppBase = CONTEXT_DIR.toString();
    EngineConfig engineConfig = new EngineConfig( webAppBase );
    ConfigurationReader.setEngineConfig( engineConfig );
  }

  public static void removeContext() {
    if( CONTEXT_DIR.exists() ) {
      delete( CONTEXT_DIR );
    }
  }
  
  public static void delete( final File toDelete ) {
    if( toDelete.isDirectory() ) {
      File[] children = toDelete.listFiles();
      for( int i = 0; i < children.length; i++ ) {
        delete( children[ i ] );
      }
    }
    toDelete.delete();
  }

  public static void copyTestResource( final String resourceName, 
                                       final File destination )
    throws FileNotFoundException, IOException
  {
    ClassLoader loader = Fixture.class.getClassLoader();
    InputStream is = loader.getResourceAsStream( resourceName );
    try {
      OutputStream out = new FileOutputStream( destination );
      try {
        int c = is.read();
        while( c != -1 ) {
          out.write( c );
          c = is.read();
        }
      } finally {
        out.close();
      }
    } finally {
      is.close();
    }
  }
  
  public static File getWebAppBase() throws Exception {
    File result = CONTEXT_DIR;
    if( !result.exists() )  {
      createContextWithoutResourceManager();
      result = CONTEXT_DIR;
    }
    return result;
  }
  
  public static void fakeBrowser( final Browser browser ) {
    ISessionStore session = ContextProvider.getSession();
    session.setAttribute( ServiceContext.DETECTED_SESSION_BROWSER, browser );
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setDetectedBrowser( browser );
  }
  
  public static void fakeRequestParam( final String key, final String value ) {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setParameter( key, value );
  }
  
  public static void fakeContextProvider( final HttpServletResponse response, 
                                          final HttpServletRequest request ) 
  {
    ServiceContext context = new ServiceContext( request, response );
    ServiceStateInfo stateInfo = new ServiceStateInfo();
    context.setStateInfo( stateInfo );
    ContextProvider.setContext( context );
  }
  
  public static void setPrivateField( final Class clazz, 
                                      final Object object, 
                                      final String fieldName, 
                                      final Object value ) 
  {
    Field[] fields = clazz.getDeclaredFields();
    Field field = null;
    for( int i = 0; field == null && i < fields.length; i++ ) {
      if( fields[ i ].getName().equals( fieldName ) ) {
        field = fields[ i ];
      }
    }
    if ( field == null ) {
      Assert.fail( "Private field "
                   + clazz.getName()
                   + "#"
                   + fieldName
                   + " could not be found." );
    }
    field.setAccessible( true );
    try {
      field.set( object, value );
    } catch( Exception e ) {
      e.printStackTrace();
      Assert.fail( "Failed to set value of private field "
                   + clazz.getName()
                   + "#"
                   + fieldName );
    } 
  }
  
  public static void setResponseWriter( final HtmlResponseWriter writer ) {
    ContextProvider.getStateInfo().setResponseWriter( writer );
  }
  
  public static String getHeadMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getHeadSize(); i++ ) {
      buffer.append( writer.getHeadToken( i ) );
    }
    return buffer.toString();
  }
  
  public static String getFootMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getFootSize(); i++ ) {
      buffer.append( writer.getFootToken( i ) );
    }
    return buffer.toString();
  }
  
  public static String getBodyMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    for( int i = 0; i < writer.getBodySize(); i++ ) {
      buffer.append( writer.getBodyToken( i ) );
    }
    return buffer.toString();
  }
  
  public static String getAllMarkup() {
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    HtmlResponseWriter writer = stateInfo.getResponseWriter();
    return getAllMarkup( writer );
  }
  
  public static String getAllMarkup( final HtmlResponseWriter writer ) {
    StringBuffer buffer = new StringBuffer();
    buffer.append( getHeadMarkup( writer ) );
    buffer.append( getBodyMarkup( writer ) );
    buffer.append( getFootMarkup( writer ) );
    return buffer.toString();
  }
  
  public static void fakeUserAgent( final String userAgent ) {
    TestRequest request = ( TestRequest )ContextProvider.getRequest();
    request.setHeader( "User-Agent", userAgent );
  }
  
  public static void fakeResponseWriter() {
    HtmlResponseWriter writer = new HtmlResponseWriter();
    IServiceStateInfo stateInfo = ContextProvider.getStateInfo();
    stateInfo.setResponseWriter( writer );
  }

  public static void fakePhase( final PhaseId phaseId ) {
    String key = CurrentPhase.class.getName() + "#value";
    ContextProvider.getStateInfo().setAttribute( key, phaseId );
  }
}