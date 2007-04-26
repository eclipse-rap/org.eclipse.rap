/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.swt.internal.engine;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.swt.resources.IResource;
import com.w4t.ParamCheck;


public final class ResourceRegistry {
  
  private static final Set resources = new HashSet();

  private ResourceRegistry() {
    // prevent instantiation
  }
  
  public static void add( final IResource resource ) {
    ParamCheck.notNull( resource, "resource" );
    resources.add( resource );
  }
  
  public static IResource[] get() {
    IResource[] result = new IResource[ resources.size() ];
    resources.toArray( result );
    return result;
  }
  
  public static void clear() {
    resources.clear();
  }
}
