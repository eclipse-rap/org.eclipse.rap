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

package org.eclipse.rap.rwt.internal.widgets;

import java.io.IOException;
import org.eclipse.rap.rwt.graphics.Font;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Item;


public class ItemLCAUtil {

  private ItemLCAUtil() {
    // prevent instantiation
  }
  
  public static void preserve( final Item item ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( item );
    adapter.preserve( Props.TEXT, item.getText() );
    adapter.preserve( Props.IMAGE, item.getImage() );
  }
  
  public static void writeText( final Item item ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( item );
    writer.set( Props.TEXT, JSConst.QX_FIELD_LABEL, item.getText(), "" );
  }
  
  public static void writeImage( final Item item ) throws IOException {
    if( WidgetUtil.hasChanged( item, Props.IMAGE, item.getImage(), null ) ) {
      String imagePath;
      if( item.getImage() == null ) {
        imagePath = "";
      } else {
        imagePath = Image.getPath( item.getImage() );
      }
      JSWriter writer = JSWriter.getWriterFor( item );
      writer.set( JSConst.QX_FIELD_ICON, imagePath );
    }
  }
  
  public static void writeFont( final Item item, final Font font ) 
    throws IOException 
  {
    WidgetLCAUtil.writeFont( item, font );
  }

  public static void writeChanges( final Item item ) throws IOException {
    writeText( item );
    writeImage( item );
  }
}
