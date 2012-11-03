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
package org.eclipse.rap.ui.resources;


/**
 * Implementations of this interface represent an existing resource to be registered, either local
 * or external.
 *
 * @since 2.0
 */
public interface IResource {

  /**
   * Specifies the classloader that should be used to load the local resource. If there is no need
   * for a particular classloader, implementations should simply return their own classloader:
   *
   * <pre>
   * ClassLoader getLoader() {
   *   return this.getClass().getClassLoader();
   * }
   * </pre>
   *
   * @return the classloader to use, or <code>null</code> for external resources
   */
  ClassLoader getLoader();

  /**
   * Returns the location of the resource. For external resources, this is the URL to load the
   * resource from. For local resources, this is the path that the classloader can read the resource
   * from.
   *
   * @return the location of the resource
   * @see IResource#isExternal()
   */
  String getLocation();

  /**
   * Indicates whether the resource is a javascript library that should be loaded by the client.
   * Applies for both, external and local resources.
   *
   * @return whether this resource is a javascript library
   * @see IResource#isExternal()
   */
  boolean isJSLibrary();

  /**
   * Indicates whether the resource is external, i.e. loaded from an external URL. In the case of an
   * external javascript library it will be included in the generated page with a &lt;script&gt;
   * tag.
   *
   * @return whether the resource is located externally
   */
  boolean isExternal();

}
