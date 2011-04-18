/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rwt.internal;

import org.eclipse.rwt.internal.lifecycle.RWTLifeCycle;

/** 
 * <p>This interface provides access to the W4 Toolkit configuration
 * settings.</p>
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IConfiguration {

  public static final String PARAM_LIFE_CYCLE = "lifecycle";
  public static final String PARAM_RESOURCES = "resources";

  public static final String LIFE_CYCLE_DEFAULT = RWTLifeCycle.class.getName();

  public static final String RESOURCES_DELIVER_FROM_DISK = "deliverFromDisk";
  public static final String RESOURCES_DELIVER_BY_SERVLET = "deliverByServlet";

  /**
   * specifies the implementation class that manages the lifecycle of each
   * request.
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
  String getResources();
}
