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
 ******************************************************************************/
package org.eclipse.rap.rwt.resources;

import java.io.InputStream;


/**
 * <p>The resource manager is responsible for registering resources
 * like images, css files etc. which are available on the applications
 * classpath. The registered files will be read out from their libraries
 * and delivered if requested. Usually resources are stored in libraries
 * in the WEB-INF/lib directory of a web-application</p>
 *
 * <p>This interface is not intended to be implemented by clients.</p>
 *
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IResourceManager {

  /**
   * Registers a given resource for download with the given name relative to the
   * context root.
   *
   * @param name filename that represents the download path relative to the
   *          applications context root.
   * @param inputStream the content of the resource to register.
   */
  void register( String name, InputStream inputStream );

  /**
   * Registers a text resource that is encoded with the given
   * <code>charset</code> for download with the given name relative to the
   * context root.
   * <p>
   * By specifying an <code>option</code> other than <code>NONE</code> the
   * resource will be versioned and/or compressed. As compressing is only
   * intended for resources that contain JavaScript, versioning might be useful
   * for other resources as well. When versioning is enabled a version number is
   * appended to the resources' name which is derived from its content.
   * </p>
   *
   * @param name filename that represents the download path relative to the
   *          applications context root.
   * @param is the content of the resource to register.
   * @param charset - the name of the charset which was used when the resource
   *          was stored. Must not be <code>null</code>.
   * @param options - an enumeration which specifies whether the resource will
   *          be versioned and/or compressed. Must not be <code>null</code>.
   */

  /**
   * Unregisters the resource with the given <code>name</code>.
   *
   * @param name the name that represents the resource. Must not be
   * <code>null</code>.
   * @return <code>true</code> is returned if unregistering the resource was
   * successful; <code>false</code> otherwise.
   * @since 1.3
   */
  boolean unregister( String name );

  /**
   * Returns whether the resource with the given name has already been
   * registered with this IResourceManager instance.
   *
   * @param name filename which identifies the registered resource. The filename
   *          must be relative to a classpath root, e.g. a gif 'my.gif' located
   *          within the package 'org.eclipse.rap' is identified as
   *          'org/eclipse/rap/my.gif'. Must not be <code>null</code>.
   * @return if the resource is already registered
   */
  boolean isRegistered( String name );

  /**
   *  Returns a location within the web-applications context where the
   *  resource will be available for the browser to download.
   *
   *  @param name name which identifies the registered resource.
   *  @return the location where the resource will be available for the browser
   */
  String getLocation( String name );

  /**
   * Returns the content of the registered resource with the given name.
   *
   * @param name the name of the resource. Must not be <code>null</code>.
   * @return an input stream to the contents of the resource, or
   *         <code>null</code> if no such resource exists
   * @since 1.1
   */
  InputStream getRegisteredContent( String name );

}
