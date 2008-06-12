/*******************************************************************************
 * Copyright (c) 2002, 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.internal.servlet;

import java.util.*;


public final class EntryPointExtension {
  
  // Maps entry point id's to their respective parameters
  // Key: id, value: parameter
  private static final Map entryPointAssociation = new HashMap();

  public static void bind( final String id, final String parameter ) {
    entryPointAssociation.put( id, parameter );
  }
  
  public static void unbindAll() {
    entryPointAssociation.clear();
  }
  
  public static String getById( final String id ) {
    return ( String )entryPointAssociation.get( id );
  }

  private EntryPointExtension() {
    // prevent instantiation
  }
}
