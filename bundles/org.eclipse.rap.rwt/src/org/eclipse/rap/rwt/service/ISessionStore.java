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
package org.eclipse.rap.rwt.service;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;


/**
 * @since 2.0
 * @deprecated Use {@link UISession} instead
 */
@Deprecated
public interface ISessionStore {

  @Deprecated
  boolean setAttribute( String name, Object value );

  @Deprecated
  Object getAttribute( String name );

  @Deprecated
  boolean removeAttribute( String name );

  @Deprecated
  Enumeration<String> getAttributeNames();

  @Deprecated
  String getId();

  @Deprecated
  boolean addSessionStoreListener( UISessionListener listener );

  @Deprecated
  boolean removeSessionStoreListener( UISessionListener listener );

  @Deprecated
  HttpSession getHttpSession();

  @Deprecated
  boolean isBound();

}
