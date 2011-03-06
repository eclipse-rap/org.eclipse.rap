/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Frank Appel - replaced singletons and static fields (Bug 337787)
 ******************************************************************************/
package org.eclipse.swt.internal.graphics;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.swt.graphics.FontData;


public class FontDataFactoryInstance {
  private final Map cache;
  
  private FontDataFactoryInstance() {
    cache = new Hashtable();
  }

  FontData findFontData( final FontData fontData ) {
    // Note [rst]: We don't need to synchronize get and put here. Since the
    //             creation of FontData is deterministic, concurrent access
    //             can at worst lead to one FontData instance overwriting the
    //             other. In this rare case, two equal internal FontData
    //             instances would be in use in the system, which is harmless.
    Object key = new Integer( fontData.hashCode() );
    FontData result = ( FontData )cache.get( key );
    if( result == null ) {
      result = cloneFontData( fontData );
      cache.put( key, result );
    }
    return result;
  }

  void clear() {
    cache.clear();
  }

  private FontData cloneFontData( final FontData fontData ) {
    String name = fontData.getName();
    int height = fontData.getHeight();
    int style = fontData.getStyle();
    return new FontData( name, height, style );
  }
}
