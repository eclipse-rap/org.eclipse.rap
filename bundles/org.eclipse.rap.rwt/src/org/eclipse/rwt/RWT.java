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

import org.eclipse.rwt.internal.lifecycle.LifeCycleFactory;
import org.eclipse.rwt.internal.resources.ResourceManager;
import org.eclipse.rwt.internal.service.*;
import org.eclipse.rwt.lifecycle.ILifeCycle;
import org.eclipse.rwt.resources.IResourceManager;
import org.eclipse.rwt.service.*;


/**
 * This class provides access to aspects of RWT which are not
 * part of the SWT API as RAP needs some additions regarding
 * the server and client communication. It is responsible for
 * providing access to the {@link ISessionStore} and the
 * {@link HttpServletRequest}.
 * 
 * @since 1.0
 * @see ILifeCycle
 * @see ISessionStore
 * @see IResourceManager
 * @see HttpServletRequest
 * @see HttpServletResponse
 */
public final class RWT {

  private static final IServiceManager serviceManager = new IServiceManager() {
    public void registerServiceHandler( final String id, 
                                        final IServiceHandler serviceHandler ) 
    {
      ServiceManager.registerServiceHandler( id, serviceHandler );
    }
    public void unregisterServiceHandler( final String id ) 
    {
      ServiceManager.unregisterServiceHandler( id );
    }
  };
  
  /**
   * Returns the instance of the current life cycle
   * which is currently processed.
   * 
   * @return instance of {@link ILifeCycle}
   */
  public static ILifeCycle getLifeCycle() {
    return LifeCycleFactory.getLifeCycle();
  }
  
  /**
   * Returns the instance of the currently available
   * {@link IResourceManager}
   * 
   * @return instance of {@link IResourceManager}
   */
  public static IResourceManager getResourceManager() {
    return ResourceManager.getInstance();
  }
  
  /**
   * Returns a manager to add and remove {@link IServiceHandler}s.
   * 
   * @return an {@link IServiceManager}
   */
  public static IServiceManager getServiceManager() {
    return serviceManager;
  }
  
  /**
   * Returns the {@link IServiceStateInfo} that is mapped
   * to the currently processed request.
   * 
   * @return {@link IServiceStore}
   */
  public static IServiceStore getServiceStore() {
    return ContextProvider.getStateInfo();
  }
  
  /**
   * Returns the <code>ISessionStore</code> of the <code>HttpSession</code>
   * to which the currently processed request belongs.
   * 
   * @return instance of {@link ISessionStore}
   */
  public static ISessionStore getSessionStore() {
    return ContextProvider.getSession();
  }
  
  /**
   * Returns the <code>HttpServletRequest</code> that is currently
   * processed.
   * 
   * @return instance of {@link HttpServletRequest}
   */
  public static HttpServletRequest getRequest() {
    return ContextProvider.getRequest();
  }
  
  /**
   * Returns the <code>HttpServletResponse</code> that is mapped
   * to the currently processed request.
   * 
   * @return instance of {@link HttpServletResponse}
   */
  public static HttpServletResponse getResponse() {
    return ContextProvider.getResponse();
  }
  
  private RWT() {
    // prevent instantiation
  }
}
