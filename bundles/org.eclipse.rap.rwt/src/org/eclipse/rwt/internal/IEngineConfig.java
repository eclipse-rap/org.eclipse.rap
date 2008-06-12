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
package org.eclipse.rwt.internal;

import java.io.File;

/**
 * This interface defines the method to be implemented by an object 
 * usable for configuration of the W4T engine adapter.
 */
public interface IEngineConfig {
  
  /**
   * This returns a file object that represent a W4T.xml config file
   * containing the configuration for the W4T engine. The file object
   * returned might differ for different engine adapters in the same VM.
   * 
   * @return A file object corresponding to a W4T config file.
   */
  public File getConfigFile();

  /**
   * This returns a file object representing a global lib directory
   * containing W4T jar files. This file object will always correspond
   * to the same directory even for different engine adapters in the same VM.
   * 
   * @return A file object corresponding to the directory where the W4T jar
   * files reside.
   */
  public File getLibDir();

  /**
   * This returns a file object representing the server context directory.
   * The server context directory is the directory where JavaScript and images
   * needed by the engine adapter reside or into which the engine
   * adapter writes generated Javascript or Image files. Thus, the directory
   * need to be writable and accessible from the webserver - both requirements
   * are, of course, up to the implementor of the configuration object.
   * Different configurations in the same VM will return file objects
   * corresponding to the same directory.
   *  
   * @return A file object corresponding to the directory where the engine
   * adapter should generate JavaScript or image files.
   */
  public File getServerContextDir();

  /**
   * This returns a file object representing the directory where the
   * form classes reside/should be written to. The directory has to be
   * writable. Configurations for different engine adapters within the same
   * VM will return file objects representing different directories. 
   *  
   * @return A file object corresponding to the directory where form
   *         classes reside.
   */
  public File getClassDir();

  /**
   * This returns a file object representing the directory where the form
   * sources reside. The directory doesn't need to be writable by the adapter.
   * Configurations for different engine adapters within the same VM will
   * return file objects representing different directories.
   * 
   * @return A file object corresponding to the directory where form 
   *         sources reside.
   */
  public File getSourceDir();
}
