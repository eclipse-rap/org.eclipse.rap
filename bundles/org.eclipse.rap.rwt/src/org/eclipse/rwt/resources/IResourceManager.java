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
package org.eclipse.rwt.resources;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;

// TODO javadoc - revise this regarding RAP / W4T
/**
 * <p>The resource manager is responsible for registering resources
 * like images, css files etc. which are available on the applications 
 * classpath. The registered files will be read out from their libraries 
 * and delivered if requested. Usually resources are stored in libraries 
 * in the WEB-INF/lib directory of a web-application</p>
 * 
 * <p>This allows to package user-defined components with all needed
 * resources into their own jars, and distribute them independently
 * from the RAP core library.</p>
 * 
 * @since 1.0
 */ 
public interface IResourceManager {
  
  /**
   * <p>An enumeration used to specify additional behavior when registering
   * a resource.</p>
   * <ul>
   *    <li><code>NONE</code> - no further action is taken.</li>
   *    <li><code>VERSION</code> - the resource to be registered will be versioned.</li>
   *    <li><code>COMPRESS</code> - the resource to be registered will be 
   *        compressed, assuming it contains JavaScript code.</li>
   *    <li><code>VERSION_AND_COMPRESS</code> - the resource to be registered will
   *        be versioned and compressed.</li>
   * </ul>
   * 
   * @since 1.0
   */
  public final class RegisterOptions {
    
    /**
     * <code>NONE</code> - no further action is taken.
     */
    public static final RegisterOptions NONE 
      = new RegisterOptions( "none" );
    
    /**
     * <code>VERSION</code> - the resource to be registered will be versioned.
     */
    public static final RegisterOptions VERSION
      = new RegisterOptions( "version" );
    
    /**
     * <code>COMPRESS</code> - the resource to be registered will be 
     *        compressed, assuming it contains JavaScript code.
     */
    public static final RegisterOptions COMPRESS
      = new RegisterOptions( "compress" );
    
    /**
     * <code>VERSION_AND_COMPRESS</code> - the resource to be registered will
     *        be versioned and compressed.
     */
    public static final RegisterOptions VERSION_AND_COMPRESS
      = new RegisterOptions( "version_and_compress" );  
    
    private static RegisterOptions[] INTERNAL_VALUES = new RegisterOptions[] { 
      NONE, VERSION, COMPRESS, VERSION_AND_COMPRESS };
    
    /**
     * Returns all available <code>RegisterOptions</code>.
     * 
     * @return array of available <code>RegisterOptions</code>
     */
    public static RegisterOptions[] values() {
      RegisterOptions[] result = new RegisterOptions[ INTERNAL_VALUES.length ];
      System.arraycopy( INTERNAL_VALUES, 0, result, 0, INTERNAL_VALUES.length );
      return result;
    }
    
    private final String name;

    private RegisterOptions( final String name ) {
      this.name = name;
    }

    public String toString() {
      return name;
    }
  }
  
  /** 
   * <p>registers a resource which is located on the web-applications
   * classpath.</p> 
   * 
   * @param name filename which identifies the resource to register. The
   *              filename must be relative to a classpath root, e.g. a gif 
   *              'my.gif' located within the package 'org.eclipse.rap' is identified
   *              as 'org/eclipse/rap/my.gif'. Must not be <code>null</code>. 
   */
  void register( String name );
  
  /** 
   * <p>registers a given resource for download with the given name relative
   * to the context root.</p>
   * 
   * @param name filename that represents the download path relative to
   *             the applications context root.
   * @param is the content of the resource to register.
   */
  void register( String name, InputStream is );
  
  /** 
   * <p>registers a text resource which is located on the web-applications
   * classpath and encoded with the given <code>charset</code>.</p> 
   *  
   * @param name filename which identifies the resource to register. The
   *              filename must be relative to a classpath root, e.g. a gif 
   *              'my.gif' located within the package 'org.eclipse.rap' is identified
   *              as 'org/eclipse/rap/my.gif' 
   * @param charset the name of the charset which was used when the resource 
   *         was stored.               
   */
  void register( String name, String charset );

  /** 
   *  <p>registers a text resource which is located on the web-applications
   *  classpath and encoded with the given <code>charset</code>.</p>
   *  
   *  <p>By specifying an <code>option</code> other than <code>NONE</code>
   *  the resource will be versioned and/or compressed. As compressing is 
   *  only intended for resources that contain JavaScript, versioning might
   *  be useful for other resources as well. When versioning is enabled a 
   *  version number is appended to the resources' name which is derived from
   *  its content.</p>
   *   
   *  @param name - filename which identifies the resource to register. The
   *              filename must be relative to a classpath root, e.g. a gif 
   *              'my.gif' located within the package 'org.eclipse.rap' is identified
   *              as 'org/eclipse/rap/my.gif'. Must not be <code>null</code>.
   *  @param charset - the name of the charset which was used when the resource 
   *         was stored. Must not be <code>null</code>.               
   *  @param options - an enumeration which specifies whether the resource will
   *         be versioned and/or compressed. Must not be <code>null</code>.
   */
  void register( String name, String charset, RegisterOptions options );
  
  /**
   * <p> registers a text resource that is encoded with the given
   * <code>charset</code> for download with the given name relative to the
   * context root.</p>
   * <p>By specifying an <code>option</code> other than <code>NONE</code> the
   * resource will be versioned and/or compressed. As compressing is only
   * intended for resources that contain JavaScript, versioning might be useful
   * for other resources as well. When versioning is enabled a version number is
   * appended to the resources' name which is derived from its content.</p>
   * 
   * @param name filename that represents the download path relative to the
   *            applications context root.
   * @param is the content of the resource to register.
   * @param charset - the name of the charset which was used when the resource
   *            was stored. Must not be <code>null</code>.
   * @param options - an enumeration which specifies whether the resource will
   *            be versioned and/or compressed. Must not be <code>null</code>.
   */
  void register( String name,
                 InputStream is,
                 String charset,
                 RegisterOptions options );

  /**
   * <p>Returns the charset of a registered resource as it was specified in
   * the call to <code>register(String,String)</code> or 
   * <code>register(String,String,ResourceOption)</code>.</p>
   * @param name - the name of the resource to obtain the charset from. Must
   * not be <code>null</code>.
   * @return the charset name or <code>null</code> if none was specified.
   */
  String getCharset( String name );
  
  /** 
   * <p>returns whether the resource with the given name has already been
   * registered with this IResourceManager instance.</p>
   *  
   * @param name filename which identifies the registered resource. The
   *             filename must be relative to a classpath root, e.g. a gif 
   *             'my.gif' located within the package 'org.eclipse.rap' is identified
   *             as 'org/eclipse/rap/my.gif'. Must not be <code>null</code>.
   *  
   * @return if the resource is already registered
   */
  boolean isRegistered( String name );
  
  /**
   *  Returns a location within the web-applications context where the 
   *  resource will be available for the browser to download.
   *  
   *  <!--
   *  <p>If the
   *  application runs in the DELIVER_FROM_DISK mode (see the configuration 
   *  file W4T.xml), the library will provide a copy of the specified resource 
   *  on the appropriate path. Otherwise this returns a path which requests the
   *  resource directly from the w4toolkit servlet.</p> 
   *  -->
   *  
   *  @param name filename which identifies the registered resource. The
   *              filename must be relative to a classpath root, e.g. a gif 
   *              'my.gif' located within the package 'org.eclipse.rap' is identified
   *              as 'org/eclipse/rap/my.gif'. Must not be <code>null</code>.
   *              
   *  @return the location where the resource will be available for the browser
   */
  String getLocation( String name );
  
  /**
   * Finds the resource with the given name. A resource is some data
   * (images, audio, text, etc) that can be accessed by class code in a way
   * that is independent of the location of the code.
   *
   * <p> Note: This method searchs the classpath of the current web-application 
   * and should be used instead of <code>ClassLoader.getResource(String)</code>
   * to avoid finding problems on some application servers that use their own
   * classloader implementations.</p>
   * 
   * @param  name
   *         A '<code>/</code>'-separated path name that identifies the 
   *         resource.
   *
   * @return  A <code>URL</code> object for reading the resource, or
   *          <code>null</code> if the resource could not be found or the
   *          invoker doesn't have adequate  privileges to get the resource.
   */
  URL getResource( String name );
  
  /**
   * Returns an input stream for reading the specified resource. A resource is 
   * some data (images, audio, text, etc) that can be accessed by class code in
   * a way that is independent of the location of the code.
   *
   * <p> Note: This method searchs the classpath of the current web-application 
   * and should be used instead of 
   * <code>ClassLoader.getResourceAsStream(String)</code>
   * to avoid finding problems on some application servers that use their own
   * classloader implementations.</p>
   *
   * @param  name
   *         A '<code>/</code>'-separated path name that identifies the 
   *         resource.
   *
   * @return  An input stream for reading the resource, or <code>null</code>
   *          if the resource could not be found.
   */
  InputStream getResourceAsStream( String name );
  
  /**
   * Finds all the resources with the given name. A resource is some data
   * (images, audio, text, etc) that can be accessed by class code in a way
   * that is independent of the location of the code.
   *
   * <p> Note: This method searchs the classpath of the current web-application 
   * and should be used instead of <code>ClassLoader.getResources(String)</code>
   * to avoid finding problems on some application servers that use their own
   * classloader implementations.</p>
   *
   * @param  name
   *         A '<code>/</code>'-separated path name that identifies the 
   *         resource.
   *
   * @return  An enumeration of {@link java.net.URL <code>URL</code>} objects 
   *          for the resource. If no resources could  be found, the enumeration
   *          will be empty.  Resources that classloaders doesn't have
   *          access to will not be in the enumeration.
   *
   * @throws  IOException
   *          If I/O errors occur
   */
  Enumeration getResources( String name ) throws IOException;

  /**
   * If resources are not available for the w4toolkit context
   * classloader, it may be necessary to specify a classloader
   * before using one of the <code>registerXXX</code> or 
   * <code>getResourceXXX</code> methods. Note that in case of the
   * <code>getResourceXXX</code> methods the calls are simply delegated
   * to the specified classloader.
   * 
   * <p>Usage:
   * <pre>
   *   IResourceManager manager = W4TContext.getResourceManager();
   *   ClassLoader contextLoader = ... // retrieve the classloader
   *   manager.setContextLoader( contextLoader );
   *   try {
   *     manager.register( "resources/images/myimage.gif" );
   *   } finally {
   *     manager.setContextLoader( null );
   *   }
   * <pre>
   * </p>
   * 
   * @param classLoader the classloader that has access to the resources
   *                    that should be loaded or registered via this
   *                    <code>IResourceManager</code> or <code>null</code>
   *                    to unset the context loader after usage.
   */
  void setContextLoader( ClassLoader classLoader );
  
  /**
   * Returns the <code>ClassLoader</code> that should be used to load
   * or register resources in case that these resources are not available
   * for the RAP context loader. May return <code>null</code> if no
   * context loader is specified.
   * 
   * @return the classloader to use
   */
  ClassLoader getContextLoader();
  
  /**
   * Returns the content of the registered resource with the given name.
   * 
   * @param name the name of the resource
   * @return an input stream to the contents of the resource, or
   *         <code>null</code> if no such resource exists
   * @since 1.1
   */
  InputStream getRegisteredContent( String name );

}
