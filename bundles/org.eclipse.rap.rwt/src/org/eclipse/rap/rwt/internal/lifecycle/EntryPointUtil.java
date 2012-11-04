/*******************************************************************************
 * Copyright (c) 2011, 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.rap.rwt.internal.application.RWTFactory;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.lifecycle.IEntryPoint;


public class EntryPointUtil {

  public static final String DEFAULT_PATH = "/rap";

  private EntryPointUtil() {
    // prevent instantiation
  }

  public static IEntryPoint getCurrentEntryPoint() {
    EntryPointRegistration registration = findCurrentEntryPointRegistration();
    return registration.getFactory().create();
  }

  public static Map<String, String> getCurrentEntryPointProperties() {
    EntryPointRegistration registration = findCurrentEntryPointRegistration();
    return registration.getProperties();
  }

  private static EntryPointRegistration findCurrentEntryPointRegistration() {
    EntryPointRegistration result = null;
    HttpServletRequest request = ContextProvider.getRequest();
    String path = request.getServletPath();
    if( path != null && path.length() > 0 ) {
      EntryPointManager entryPointManager = RWTFactory.getEntryPointManager();
      result = entryPointManager.getRegistrationByPath( path );
    }
    if( result == null ) {
      throw new IllegalArgumentException( "Entry point not found: " + path );
    }
    return result;
  }

}
