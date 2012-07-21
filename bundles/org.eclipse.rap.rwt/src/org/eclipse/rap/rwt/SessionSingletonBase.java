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
package org.eclipse.rap.rwt;

import org.eclipse.rap.rwt.internal.SingletonManager;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.util.ParamCheck;
import org.eclipse.rap.rwt.service.ISessionStore;


/**
 * <code>SessionSingletonBase</code> creates and manages a unique instance of a given type
 * with session scope. This means that in the context of one user session
 * <code>getInstance(Class)</code> will always return the same object, but for different user
 * sessions the returned instances will be different.
 *
 * <p>Usage:
 * <pre>
 * public class FooSingleton {
 *
 *   private FooSingleton() {}
 *
 *   public static FooSingleton getInstance() {
 *     return ( FooSingleton )SessionSingletonBase.getInstance( FooSingleton.class );
 *   }
 * }
 * </pre>
 * </p>
 *
 * @since 2.0
 */
public abstract class SessionSingletonBase {

  /**
   * Returns the singleton instance of the specified type that is stored
   * in the current session context. If no instance exists yet, a new
   * one will be created. The specified type must have a parameterless
   * constructor.
   *
   * @param type specifies the session singleton instance type.
   * @return the unique instance of the specified type that is associated
   *         with the current user session context.
   */
  public static <T> T getInstance( Class<T> type ) {
    ParamCheck.notNull( type, "type" );
    ISessionStore sessionStore = ContextProvider.getSessionStore();
    return SingletonManager.getInstance( sessionStore ).getSingleton( type );
  }
}
