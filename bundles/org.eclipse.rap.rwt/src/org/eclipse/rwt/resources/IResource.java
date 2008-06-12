/*******************************************************************************
 * Copyright (c) 2002, 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.resources;

import org.eclipse.rwt.resources.IResourceManager.RegisterOptions;

/**
 * Implementations of this interface represent an existing resource - local
 * or external. 
 * 
 * @since 1.0
 */
public interface IResource {
  
  /**
   * Specifies the classloader that has access to the resource
   * that should be loaded. If there is no special classloader you
   * want to use just return the one of your implementation:
   * 
   * <code>
   * ClassLoader getLoader() {
   *    return this.getClass().getClassLoader();
   * }
   * </code>
   * 
   * @return the classloader to use
   */
  ClassLoader getLoader();
  
  /**
   * Returns the location of the resource. This can be a path to a file
   * within the bundles classpath or a URI for an external resource.
   * 
   * @return the location of the resource
   * 
   * @see IResource#isExternal()
   */
  String getLocation();
  
  /**
   * Specifies the charset to use for this resource.
   * 
   * @see HTML
   * 
   * @return the charset to use
   */
  String getCharset();
  
  /**
   * Specifies in which way the resource is delivered. This is interesting
   * for javascript libraries to compress and version them before sending
   * them to the client. If this resource is not an javascript library use
   * <code>RegisterOptions#NONE</code>. If you want RAP to compress the file
   * use <code>RegisterOptions#COMPRESS</code> and to version it use
   * <code>RegisterOptions#VERSION</code>.
   * If you want to combine versioning and compression you can return
   * <code>RegisterOptions#VERSION_AND_COMPRESS</code> as value.
   * 
   * @return an instance of <code>RegisterOptions</code>
   * 
   * @see IResourceManager.RegisterOptions
   */
  RegisterOptions getOptions();
  
  /**
   * Decides whether the resource is a javascript library and thus handled
   * a bit different than other resources. This is also needed if you plan to 
   * have external, compressed and versioned javascript libraries.
   * 
   * @return whether this resource is a javascript library
   * 
   * @see IResourceManager.RegisterOptions
   * @see IResource#isExternal()
   * @see IResource#getOptions()
   */
  boolean isJSLibrary();
  
  /**
   * Decides whether the resource is external or not. If the resource
   * is a javascript library and not external it will be transmitted
   * immediately with all the other resources. In the case of an external
   * javascript library it will be included in the generated page with
   * a &lt;script&gt; tag.
   * 
   * @return whether the resource is located externally
   */
  boolean isExternal();
}
