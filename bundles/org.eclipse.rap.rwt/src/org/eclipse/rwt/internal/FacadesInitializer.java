/*******************************************************************************
 * Copyright (c) 2008, 2011 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal;

import org.eclipse.rwt.internal.util.ClassUtil;

public final class FacadesInitializer {

  public static Object load( final Class facade ) {
    String name = facade.getName();
    return ClassUtil.newInstance( facade.getClassLoader(), name + "Impl" );
  }
}
