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
package org.eclipse.rap.rwt.graphics;

import java.util.*;
import org.eclipse.rap.rwt.resources.ResourceManager;
import com.w4t.IResourceManager;
import com.w4t.ParamCheck;


public final class Image {
  
  private static final Map images = new HashMap();
  
  private Image () {
    // prevent instantiation from outside
  }
  
  public static synchronized Image find( final String path ) {
    return find( path, null );
  }
  
  public static synchronized Image find( final String path, 
                                         final ClassLoader imageLoader )
  {
    ParamCheck.notNull( path, "path" );
    Image result;
    if( images.containsKey( path ) ) {
      result = ( Image )images.get( path );
    } else {
      result = createImage( path, imageLoader );
    }
    return result;
  }
  
  public static synchronized int size(){
    return images.size();
  }
  
  public static synchronized void clear(){
    // TODO: [GR]can we deregister ressources?
//    Iterator it = images.keySet().iterator();
//    while ( it.hasNext() ) {
//      manager.deregister((String)it.next());
//    }
    images.clear();
  }
  
  public static synchronized String getPath ( final Image image ) {
    String result = null;
    if ( images.containsValue( image ) ) {
      Iterator it = images.entrySet().iterator();
      boolean next = true;
      while( next && it.hasNext() ) {
        Map.Entry entry = ( Map.Entry )it.next();
        if( entry.getValue().equals( image ) ) {
          result = ( String )entry.getKey();
          next = false;
        }
      }
    }
    return result;
  }

  //////////////////
  // helping methods
  
  private static Image createImage( final String path, 
                                    final ClassLoader imageLoader )
  {
    Image image = new Image();
    IResourceManager manager = ResourceManager.getInstance();
    ClassLoader loaderBuffer = manager.getContextLoader();
    manager.setContextLoader( imageLoader );
    try {
      manager.register( path );
    } finally {
      manager.setContextLoader( loaderBuffer );
    }
    images.put( path, image );
    return image;
  }
}