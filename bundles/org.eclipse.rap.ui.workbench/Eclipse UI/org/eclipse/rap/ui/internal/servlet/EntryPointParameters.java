/*******************************************************************************
 * Copyright (c) 2007, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.ui.internal.servlet;

import java.util.*;


public final class EntryPointParameters {

  private static final Map<String, String> parametersById = new HashMap<String, String>();

  public static void register( String id, String parameter ) {
    parametersById.put( id, parameter );
  }

  public static void clear() {
    parametersById.clear();
  }

  public static String getById( String id ) {
    return parametersById.get( id );
  }

  private EntryPointParameters() {
    // prevent instantiation
  }
}
