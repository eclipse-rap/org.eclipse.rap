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
package org.eclipse.rap.rwt.internal.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.rap.rwt.client.service.JavaScriptLoader;
import org.eclipse.rap.rwt.internal.protocol.ProtocolMessageWriter;
import org.eclipse.rap.rwt.internal.service.ContextProvider;


public class JavaScriptLoaderImpl implements JavaScriptLoader {

  private final Set<String> loadedUrls = new HashSet<String>();

  public void require( String url ) {
    List<String> urlsToLoad = new ArrayList<String>();
    if( !loadedUrls.contains( url ) ) {
      urlsToLoad.add( url );
      loadedUrls.add( url );
    }
    load( urlsToLoad );
  }

  private void load( List<String> urls ) {
    if( !urls.isEmpty() ) {
      ProtocolMessageWriter writer = ContextProvider.getProtocolWriter();
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( "files", urls.toArray() );
      writer.appendCall( "rwt.client.JavaScriptLoader", "load", properties );
    }
  }

}
