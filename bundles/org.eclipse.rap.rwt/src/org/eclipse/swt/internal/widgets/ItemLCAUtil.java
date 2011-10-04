/*******************************************************************************
 * Copyright (c) 2002, 2011 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rwt.lifecycle.WidgetLCAUtil.renderProperty;

import java.io.IOException;

import org.eclipse.rwt.internal.lifecycle.JSConst;
import org.eclipse.rwt.lifecycle.*;
import org.eclipse.swt.widgets.Item;


public class ItemLCAUtil {

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";

  private ItemLCAUtil() {
    // prevent instantiation
  }

  public static void preserve( Item item ) {
    preserveProperty( item, PROP_TEXT, item.getText() );
    preserveProperty( item, PROP_IMAGE, item.getImage() );
  }

  public static void renderChanges( Item item ) {
    renderProperty( item, PROP_TEXT, item.getText(), "" );
    renderProperty( item, PROP_IMAGE, item.getImage(), null );
  }

  public static void writeText( Item item, boolean escapeMnemonics, boolean replaceNewLines )
    throws IOException
  {
    String text = item.getText();
    if( WidgetLCAUtil.hasChanged( item, PROP_TEXT, text ) ) {
      JSWriter writer = JSWriter.getWriterFor( item );
      text = WidgetLCAUtil.escapeText( text, escapeMnemonics );
      if( replaceNewLines ) {
        text = WidgetLCAUtil.replaceNewLines( text, "<br/>" );
      }
      writer.set( JSConst.QX_FIELD_LABEL, text );
    }
  }

  public static void writeImage( Item item ) throws IOException {
    WidgetLCAUtil.writeImage( item, PROP_IMAGE, JSConst.QX_FIELD_ICON, item.getImage() );
  }

  public static void writeChanges( Item item ) throws IOException {
    writeText( item, false, false );
    writeImage( item );
  }

}
