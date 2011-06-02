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

import org.eclipse.rwt.internal.util.SharedInstanceBuffer;
import org.eclipse.rwt.internal.util.SharedInstanceBuffer.IInstanceCreator;
import org.eclipse.swt.graphics.FontData;


public class FontDataFactory {
  private final SharedInstanceBuffer<Integer,FontData> cache;
  
  public FontDataFactory() {
    cache = new SharedInstanceBuffer<Integer,FontData>();
  }

  public FontData findFontData( final FontData fontData ) {
    // Note [rst]: We don't need to synchronize get and put here. Since the
    //             creation of FontData is deterministic, concurrent access
    //             can at worst lead to one FontData instance overwriting the
    //             other. In this rare case, two equal internal FontData
    //             instances would be in use in the system, which is harmless.
    Integer key = new Integer( fontData.hashCode() );
    FontData result = cache.get( key, new IInstanceCreator<FontData>() {
      public FontData createInstance() {
        return cloneFontData( fontData );
      }
    } );
    return result;
  }

  private static FontData cloneFontData( FontData fontData ) {
    String name = fontData.getName();
    int height = fontData.getHeight();
    int style = fontData.getStyle();
    return new FontData( name, height, style );
  }
}
