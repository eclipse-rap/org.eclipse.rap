/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.resources;

import java.util.HashMap;
import java.util.Map;

public class JavaScriptModuleRegistry {

  private Map<Class<? extends JavaScriptModule>,JavaScriptModule> map
   = new HashMap<Class<? extends JavaScriptModule>,JavaScriptModule>();

  public void put( JavaScriptModule module ) {
    map.put( module.getClass(), module );
  }

  public JavaScriptModule get( Class<? extends JavaScriptModule> clazz ) {
    return map.get( clazz );
  }

}