/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.resources;

import java.io.*;
import java.net.*;
import java.text.MessageFormat;
import java.util.*;

import org.eclipse.rwt.Adaptable;
import org.eclipse.rwt.internal.ConfigurationReader;
import org.eclipse.rwt.internal.IEngineConfig;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.internal.service.RequestParams;
import org.eclipse.rwt.internal.util.*;
import org.eclipse.rwt.resources.IResourceManager;


/** <p>The resource manager is responsible for registering resources
 *  like images, css files etc. which are available on the applications 
 *  classpath. The registered files will be read out from their libraries 
 *  and delivered if requested. Usually resources are stored in libraries 
 *  in the WEB-INF/lib directory of a web-application</p>
 *  <p>Implementation as Singleton.</p>
 *  <p>This class is not intended to be used by clients.</p>
 */
public class ResourceManagerImpl
  extends ResourceBase
  implements IResourceManager, Adaptable
{
  
  /** <p>The singleton instance of ResourceManager.</p> */
  private static IResourceManager _instance;
  
  private final String webAppRoot;
  private final Map repository;
  private final Map cache;
  private ClassLoader loader;
  private ThreadLocal contextLoader;
  private JsConcatenator jsConcatenator;

  private static final class Resource {
    /** the 'raw' content of the resource. In case of a text resource (charset 
     * was given) the content is UTF-8 encoded. */
    private final int[] content;
    /** the charset in which the resource was encoded before red or null for 
     * binary resources. */
    private final String charset;
    /** the reources' version or null for 'no version' */
    private final Integer version;
    
    public Resource( final int[] content, 
                     final String charset, 
                     final Integer version ) 
    {
      this.charset = charset;
      this.content = content;
      this.version = version;
    }
    
    public String getCharset() {
      return charset;
    }
    
    public int[] getContent() {
      return content;
    }
    
    public Integer getVersion() {
      return version;
    }
  }
  
  private ResourceManagerImpl( final String webAppRoot ) {
    this.webAppRoot = webAppRoot;
    repository = new Hashtable();
    cache = new Hashtable();
    contextLoader = new ThreadLocal();
  }
  
  /** <p>Returns the singleton instance of ResourceManager.</p> */
  public static synchronized IResourceManager createInstance( 
    final String webAppRoot,
    final String mode ) 
  {
    if( _instance == null ) {
      _instance = new ResourceManagerImpl( webAppRoot );
      setDeliveryMode( mode );
    }
    return _instance;
  }
  
  /** <p>Retruns the singleton instance of <code>IResourceManager</code>.</p> */
  public static IResourceManager getInstance() {
    return _instance;
  }  
  
  /**
   * <p>Loads the given <code>resource</code> from the class path.</p> 
   * @param resource the name of the resource to be loaded. Must not be
   * <code>null</code>
   */
  public static String load( final String resource ) {
    ParamCheck.notNull( resource, "resource" );
    return ( ( ResourceManagerImpl )_instance ).doLoad( resource );    
  }
   
  /**
   * <p>Returns the content of the resource denoted by <code>name</code>.</p>
   * @param name the name of the resource to find, must not be 
   * <code>null</code>.
   * @param version the version (can be obtained by {@link #findVersion(String)}
   * <code>findVersion(String)</code>) of the resource or <code>null</code> 
   * if the resource is unversioned.
   * @return the content of the resource or <code>null</code> if no resource
   * with the given <code>name</code> and <code>version</code> exists. 
   */
  public static int[] findResource( final String name, final Integer version ) {
    ParamCheck.notNull( name, "name" );
    int[] result = null;
    ResourceManagerImpl manager = ( ResourceManagerImpl )_instance;
    Resource resource = ( Resource )manager.cache.get( createKey( name ) );
    if( resource != null ) {
      if(    ( version == null && resource.getVersion() == null ) 
          || ( version != null && version.equals( resource.getVersion() ) ) ) 
      {
        result = resource.getContent();
      }
    }
    return result;
  }

  /**
   * <p>Returns the version number for the previously {@link 
   * #register(String, String, RegisterOptions) registered} resource.</p>
   * @param name the name of the resource for which the version number should be
   * obtained. Must not be <code>null</code>.
   * @return the version number or <code>null</code> if either no such resource 
   * was registered or the resource does not have a version number.
   * @throws NullPointerException when <<code>name</code> is <code>null</code>.
   */
  public static Integer findVersion( final String name ) {
    ParamCheck.notNull( name, "name" );
    Integer result = null;
    ResourceManagerImpl manager = ( ResourceManagerImpl )_instance;
    Resource resource = ( Resource )manager.cache.get( createKey( name ) );
    if( resource != null ) {
      result = resource.getVersion();
    }
    return result;
  }

  
  //////////////////////
  // interface Adaptable
  
  public Object getAdapter( final Class adapter ) {
    Object result = null;
    if( adapter == JsConcatenator.class ){
      if( jsConcatenator == null ) {
        jsConcatenator = new JsConcatenator() {
          private String content;
          private boolean registered = false;
          public String getLocation() {
            String concatedName = "rap.js";
            if( !registered ) {
              byte[] content = getContent().getBytes();
              register( concatedName,
                        new ByteArrayInputStream( content ),
                        HTML.CHARSET_NAME_UTF_8,
                        RegisterOptions.VERSION );
              registered = true;
            }
            return ResourceManagerImpl.this.getLocation( concatedName );
          }

          public void startJsConcatenation() {
            ResourceUtil.startJsConcatenation();
          }
          public String getContent() {
            if( content == null ) {
              content = ResourceUtil.getJsConcatenationContentAsString();
            }
            return content;
          }
        };
      }
      result = jsConcatenator;
    }
    return result;
  }
  
  
  /////////////////////////////
  // interface IResourceManager
  
  public void register( final String name ) {
    ParamCheck.notNull( name, "name" );
    doRegister( name, null, RegisterOptions.NONE );
  }
  
  public void register( final String name, final String charset ) 
  {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( charset, "charset" );
    doRegister( name, charset, RegisterOptions.NONE );
  }

  public void register( final String name, 
                        final String charset, 
                        final RegisterOptions options ) 
  {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( charset, "charset" );
    ParamCheck.notNull( options, "options" );
    doRegister( name, charset, options );
  }
  
  public void register( final String name, final InputStream is ) {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( is, "is" );
    String key = createKey( name );
    try {
      int[] content = ResourceUtil.readBinary( is );
      doRegister( name, null, RegisterOptions.NONE, key, content );
    } catch ( IOException e ) {
      String text = "Failed to register resource ''{0}''.";
      String msg = MessageFormat.format( text, new Object[] { name } );
      throw new ResourceRegistrationException( msg, e ) ;
    }
    repository.put( key, name );
  }

  public void register( final String name,
                        final InputStream is, 
                        final String charset, 
                        final RegisterOptions options ) 
  {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( is, "is" );
    ParamCheck.notNull( charset, "charset" );
    ParamCheck.notNull( options, "options" );
    boolean compress = shouldCompress( options );
    String key = createKey( name );
    try {
      int[] content = ResourceUtil.read( is, charset, compress );
      doRegister( name, charset, options, key, content );
    } catch ( IOException e ) {
      String text = "Failed to register resource ''{0}''.";
      String msg = MessageFormat.format( text, new Object[] { name } );
      throw new ResourceRegistrationException( msg, e ) ;
    }
    repository.put( key, name );
  }
  
  public String getCharset( final String name ) {
    ParamCheck.notNull( name, "name" );
    Resource resource = ( Resource )cache.get( createKey( name ) );
    return resource.getCharset();
  }
  
  public boolean isRegistered( final String name ) {
    ParamCheck.notNull( name, "name" );
    String key = createKey( name );
    String fileName = ( String )repository.get( key );
    return fileName != null;    
  }

  public String getLocation( final String name ) {
    ParamCheck.notNull( name, "name" );
    String key = createKey( name );
    String fileName = ( String )repository.get( key );
    Assert.isNotNull( fileName, "No resource registered for key " + name );
    return createRequestURL( fileName, findVersion( name ) );
  }
  
  public URL getResource( final String name ) {
    return getLoader().getResource( name );
  }

  public InputStream getResourceAsStream( final String name ) {
    URL resource = getLoader().getResource( name );
    InputStream result = null;
    if( resource != null ) {
      try {
        URLConnection connection = resource.openConnection();
        connection.setUseCaches( false );
        result = connection.getInputStream();
      } catch( final IOException ignore ) {
        // ignore
      }
    }
    return result;
  }

  public Enumeration getResources( final String name ) throws IOException {
    return getLoader().getResources( name );
  }

  public void setContextLoader( final ClassLoader classLoader ) {
    contextLoader.set( classLoader );
  }

  public ClassLoader getContextLoader() {
    return ( ClassLoader )contextLoader.get();
  }

  public InputStream getRegisteredContent( final String name ) {
    InputStream result = null;
    String key = createKey( name );
    String fileName = ( String )repository.get( key );
    if( fileName != null ) {
      // TODO [rst] Works only for non-versioned content for now
      File file = getDiskLocation( name, null );
      try {
        result = new FileInputStream( file );
      } catch( FileNotFoundException e ) {
        // should not happen
        throw new RuntimeException( e );
      }
    }
    return result;
  }
  
  //////////////////
  // helping methods

  private ClassLoader getLoader() {
    ClassLoader result = loader;
    if(    getContextLoader() != null
        && getContextLoader() != ResourceManagerImpl.class.getClassLoader() )
    {
      result = getContextLoader();
    } else if( loader == null ) {
      IEngineConfig engineConfig = ConfigurationReader.getEngineConfig();
      List buffer = WebAppURLs.getWebAppURLs( engineConfig );
      URL[] urls = new URL[ buffer.size() ];
      buffer.toArray( urls );
      ClassLoader parent = getClass().getClassLoader();
      loader = new URLClassLoader( urls, parent );
      result = loader;
    }
    return result;
  }
  
  private static String createKey( final String name ) {
    return String.valueOf( name.hashCode() );
  }

  private static String createRequestURL( final String fileName, 
                                          final Integer version ) 
  {
    String result;
    String newFileName = fileName.replace( '\\', '/' );
    if( isDeliveryMode( DELIVER_FROM_DISK ) ) {
      StringBuffer url = new StringBuffer();
      url.append( URLHelper.getContextURLString() );
      url.append( "/" );
      url.append( versionedResourceName( newFileName, version ) );
      result = url.toString();
    } else {
      StringBuffer url = new StringBuffer();
      url.append( URLHelper.getURLString( false ) );
      URLHelper.appendFirstParam( url, RequestParams.RESOURCE, newFileName );
      if( version != null ) {
        URLHelper.appendParam( url, 
                               RequestParams.RESOURCE_VERSION, 
                               String.valueOf( version.intValue() ) );
      }
      result = ContextProvider.getResponse().encodeURL( url.toString() );
    }
    return result;
  }
  
  private String doLoad( final String resource ) {
    String key = createKey( resource );
    if( !repository.containsKey( key ) ) {
      try {
        register( resource );
      } catch( ResourceRegistrationException e ) {
        // application file which is not managed by the resource manager
        repository.put( key, resource );
      }
    }
    return createRequestURL( resource, null );
  }

  private void doRegister( final String name, 
                           final String charset,
                           final RegisterOptions options )
  {
    String key = createKey( name );
    // TODO [rh] should throw exception if contains key but has different
    //      charset or options
    if( !repository.containsKey( key ) ) {
      boolean compress = shouldCompress( options );
      try {
        int[] content = ResourceUtil.read( name, charset, compress );
        doRegister( name, charset, options, key, content );
      } catch ( IOException e ) {
        String text = "Failed to register resource ''{0}''.";
        String msg = MessageFormat.format( text, new Object[] { name } );
        throw new ResourceRegistrationException( msg, e ) ;
      }
      repository.put( key, name );
    }
  }

  private void doRegister( final String name, 
                           final String charset, 
                           final RegisterOptions options, 
                           final String key, 
                           final int[] content )
    throws IOException
  {
    Integer version = computeVersion( content, options );
    if( isDeliveryMode( DELIVER_FROM_DISK ) ) {
      File location = getDiskLocation( name, version );
      createFile( location );
      ResourceUtil.write( location, content );
      cache.put( key, new Resource( null, charset, version ) );
    } else if( isDeliveryMode( DELIVER_BY_SERVLET ) ) {
      cache.put( key, new Resource( content, charset, version ) );
    } else if( isDeliveryMode( DELIVER_BY_SERVLET_AND_TEMP_DIR ) ) {
      File location = getTempLocation( name, version );
      createFile( location );
      ResourceUtil.write( location, content );
      cache.put( key, new Resource( content, charset, version ) );
    }
  }
  
  private static void createFile( final File fileToWrite ) throws IOException {
    File dir = new File( fileToWrite.getParent() );
    if( !dir.exists() ) {
      if( !dir.mkdirs() ) {
        throw new IOException( "Could not create directory structure: " + dir );
      }
    }
    if( !fileToWrite.exists() ) {
      fileToWrite.createNewFile();
    }
  }
  
  private static Integer computeVersion( final int[] content, 
                                         final RegisterOptions options ) 
  {
    Integer result = null;
    if( content != null && shouldVersion( options ) ) {
      int version = 0;
      for( int i = 0; i < content.length; i++ ) {
        version = version * 31 + content[ i ];
      }
      result = new Integer( version );
    }
    return result;
  }

  private static boolean shouldVersion( final RegisterOptions options ) {
    return    (    options == RegisterOptions.VERSION 
                || options == RegisterOptions.VERSION_AND_COMPRESS )
           && SystemProps.useVersionedJavaScript();
  }
  
  static String versionedResourceName( final String name, 
                                       final Integer version ) 
  {
    String result = name;
    if( version != null ) {
      String versionString = String.valueOf( version.intValue() );
      int dotPos = name.lastIndexOf( '.' );
      // ensure that the dot was found in name part (not path)
      if( name.replace( '\\', '/' ).lastIndexOf( "/" ) > dotPos  ) {
        dotPos = -1;
      }
      if( dotPos == -1 ) {
        // append version number if not suffix
        result = name + versionString;
      } else {
        // insert version number between name and suffix 
        result =   name.substring( 0, dotPos )
                 + versionString
                 + name.substring( dotPos );
      }
    }
    return result;
  }

  private static boolean shouldCompress( final RegisterOptions options ) {
    return    (    options == RegisterOptions.COMPRESS 
                || options == RegisterOptions.VERSION_AND_COMPRESS )
           && SystemProps.useCompressedJavaScript();
  }
  
  private File getDiskLocation( final String name, final Integer version ) {
    StringBuffer filename = new StringBuffer();
    filename.append( webAppRoot );
    filename.append( File.separator );
    filename.append( versionedResourceName( name, version ) );
    return new File( filename.toString() );
  }
  
  private static File getTempLocation( final String name, 
                                       final Integer version ) 
  {
    StringBuffer result = new StringBuffer();
    result.append( System.getProperty( "java.io.tmpdir" ) );
    result.append( File.separator );
    result.append( System.getProperty( "user.name" ) );
    result.append( File.separator );
    result.append( "w4toolkit" );
    result.append( File.separator );
    result.append( versionedResourceName ( name, version ) );
    return new File( result.toString() );
  }
}
