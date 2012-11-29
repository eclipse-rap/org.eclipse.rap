/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.rap.rwt.SingletonUtil;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.service.JavaScriptLoader;


public class JavaScriptLoaderImpl implements JavaScriptLoader {

  public void ensure( String... url ) {
    List< String > files = new ArrayList< String >();
    JavaScriptSessionFiles sessionFiles = getSessionFiles();
    for( String file : url ) {
      if( !sessionFiles.has( file ) ) {
        files.add( file );
        sessionFiles.add( file );
      }
    }
    load( files );
  }

  private void load( List<String> files ) {
    if( files.size() > 0 ) {
      ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( "files", files.toArray() );
      writer.appendCall( "rwt.client.JavaScriptLoader", "load", properties );
    }
  }

  private static JavaScriptSessionFiles getSessionFiles() {
    return SingletonUtil.getSessionInstance( JavaScriptSessionFiles.class );
  }

  static private class JavaScriptSessionFiles {

    private Map<String, Boolean> map = new HashMap<String, Boolean>();

    public void add( String url ) {
      map.put( url, Boolean.TRUE );
    }

    public boolean has( String url ) {
      return map.containsKey( url );
    }

  }

}
