/*******************************************************************************
 * Copyright (c) 2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.internal.lifecycle.ILifeCycle;
import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.ContextProvider;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.IServiceStore;
import org.eclipse.rwt.service.ISessionStore;


/**
 * TODO [rh] JavaDoc 
 */
public final class RWT {

  public static ILifeCycle getLifeCycle() {
    return LifeCycleFactory.getLifeCycle();
  }
  
  public static IResourceManager getResourceManager() {
    return ResourceManager.getInstance();
  }
  
  public static IServiceStore getServiceStore() {
    return ContextProvider.getStateInfo();
  }
  
  public static ISessionStore getSessionStore() {
    return ContextProvider.getSession();
  }
  
  public static HttpServletRequest getRequest() {
    return ContextProvider.getRequest();
  }
  
  public static HttpServletResponse getResponse() {
    return ContextProvider.getResponse();
  }
  
  private RWT() {
    // prevent instantiation
  }
}
