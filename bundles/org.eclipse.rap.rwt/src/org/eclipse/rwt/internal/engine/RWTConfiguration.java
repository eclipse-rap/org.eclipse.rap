/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.engine;

import java.io.File;

import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;


public interface RWTConfiguration {
  public static final String PARAM_LIFE_CYCLE = "lifecycle";
  public static final String PARAM_RESOURCES = "resources";
  public static final String LIFE_CYCLE_DEFAULT = RWTLifeCycle.class.getName();
  public static final String RESOURCES_DELIVER_FROM_DISK = "deliverFromDisk";
  public static final String RESOURCES_DELIVER_BY_SERVLET = "deliverByServlet";

  /**
   * returns the path to the web application context on disk 
   */
  File getContextDirectory();

  /**
   * returns the path to the lib directory of the web application context 
   */
  File getLibraryDirectory();

  /**
   * returns the path to the class directory of the web application context 
   */
  File getClassDirectory();

  /**
   * returns the fully qualified class name that manages the lifecycle of each request.
   */
  String getLifeCycle();

  /**
   * <p>Returns whether static resources like JavaScript-libraries, images,
   * css-files etc. which are available on the applications
   * classpath are copied to disk and delivered as static files by
   * a web-server or delivered directly by the servlet engine.
   * Should be <code>RESOURCES_DELIVER_FROM_DISK</code> in most cases. 
   * Can be <code>RESOURCES_DELIVER_FROM_DISK</code> or 
   * <code>RESOURCES_DELIVER_BY_SERVLET</code>.
   */
  String getResourcesDeliveryMode();
}