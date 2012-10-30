/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.rap.rwt.internal.RWTProperties;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.resources.IResourceManager;


/**
 * The resource manager is responsible for registering resources like images,
 * CSS files etc. which are available on the application's classpath. The
 * registered files will be read out from their libraries and delivered if
 * requested. Usually resources are stored in libraries in the WEB-INF/lib
 * directory of a web-application
 * <p>
 * Implementation as singleton.
 * </p>
 * <p>
 * This class is not intended to be used by clients.
 * </p>
 */
public class ResourceManagerImpl implements IResourceManager {

  private final ResourceDirectory resourceDirectory;
  private final Set<String> resources;

  public ResourceManagerImpl( ResourceDirectory resourceDirectory ) {
    this.resourceDirectory = resourceDirectory;
    this.resources = Collections.synchronizedSet( new HashSet<String>() );
  }

  /////////////////////////////
  // interface IResourceManager

  public void register( String path, InputStream inputStream ) {
    ParamCheck.notNull( path, "name" );
    ParamCheck.notNull( inputStream, "inputStream" );
    checkPath( path );
    internalRegister( path, inputStream, null, RegisterOptions.NONE );
  }

  public void register( String name, InputStream is, String charset, RegisterOptions options ) {
    ParamCheck.notNull( name, "name" );
    ParamCheck.notNull( is, "is" );
    ParamCheck.notNull( charset, "charset" );
    ParamCheck.notNull( options, "options" );
    internalRegister( name, is, charset, options );
  }

  public boolean unregister( String name ) {
    ParamCheck.notNull( name, "name" );
    boolean result = false;
    if( resources.remove( name ) ) {
      result = true;
      File file = getDiskLocation( name );
      file.delete();
    }
    return result;
  }

  public boolean isRegistered( String name ) {
    ParamCheck.notNull( name, "name" );
    return resources.contains( name );
  }

  public String getLocation( String name ) {
    ParamCheck.notNull( name, "name" );
    if( !resources.contains( name ) ) {
      throw new IllegalArgumentException( "Resource does not exist: " + name );
    }
    return createRequestUrl( name );
  }

  public InputStream getRegisteredContent( String name ) {
    ParamCheck.notNull( name, "name" );
    InputStream result = null;
    if( resources.contains( name ) ) {
      File file = getDiskLocation( name );
      try {
        result = new FileInputStream( file );
      } catch( FileNotFoundException fnfe ) {
        throw new RuntimeException( fnfe );
      }
    }
    return result;
  }

  //////////////////
  // helping methods

  private String createRequestUrl( String resourceName ) {
    String newFileName = resourceName.replace( '\\', '/' );
    StringBuilder url = new StringBuilder();
    url.append( ResourceDirectory.DIRNAME );
    url.append( "/" );
    String escapedResourceNamea = escapeResourceName( newFileName );
    url.append( escapedResourceNamea );
    return url.toString();
  }

  private void internalRegister( String name,
                                 InputStream is,
                                 String charset,
                                 RegisterOptions options )
  {
    boolean compress = shouldCompress( options );
    try {
      byte[] content = ResourceUtil.read( is, charset, compress );
      registerContent( name, charset, options, content );
    } catch ( IOException ioe ) {
      throw new RuntimeException( "Failed to register resource: " + name, ioe ) ;
    }
  }

  private void registerContent( String name,
                                String charset,
                                RegisterOptions options,
                                byte[] content )
    throws IOException
  {
    File location = getDiskLocation( name );
    createFile( location );
    ResourceUtil.write( location, content );
    resources.add( name );
  }

  private static void createFile( File fileToWrite ) throws IOException {
    File dir = new File( fileToWrite.getParent() );
    if( !dir.mkdirs() ) {
      if( !dir.exists() ) {
        throw new IOException( "Could not create directory structure: " + dir.getAbsolutePath() );
      }
    }
    if( !fileToWrite.exists() ) {
      fileToWrite.createNewFile();
    }
  }

  private static boolean shouldCompress( RegisterOptions options ) {
    return    (    options == RegisterOptions.COMPRESS
                || options == RegisterOptions.VERSION_AND_COMPRESS )
           && RWTProperties.useCompressedJavaScript()
           && !RWTProperties.isDevelopmentMode();
  }

  private File getDiskLocation( String resourceName ) {
    String escapedResourceName = escapeResourceName( resourceName );
    return new File( resourceDirectory.getDirectory(), escapedResourceName );
  }

  //////////////////
  // helping methods
  
  private static void checkPath( String path ) {
    if( path.length() == 0 ) {
      throw new IllegalArgumentException( "Path must not be empty" );
    }
    if( path.endsWith( "/"  ) || path.endsWith( "\\" ) ) {
      throw new IllegalArgumentException( "Path must not end with path separator" );
    }
  }

  private static String escapeResourceName( String name ) {
    String result = name;
    result = name.replaceAll( "\\$", "\\$\\$" );
    result = result.replaceAll( ":", "\\$1" );
    result = result.replaceAll( "\\?", "\\$2" );
    return result;
  }

}
