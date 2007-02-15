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

package org.eclipse.rap.rwt.internal.widgets.buttonkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.events.SelectionEvent;
import org.eclipse.rap.rwt.graphics.Image;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Button;


final class ButtonLCAUtil {

  private static final String PARAM_SELECTION = "selection";

  private static final String PROP_ALIGNMENT = "alignment";
  private static final String PROP_SELECTION = "selection";

  private static final Integer DEFAULT_ALIGNMENT = new Integer( RWT.CENTER );
  
  private ButtonLCAUtil() {
    // prevent instantiation
  }
  
  static void readSelection( final Button button ) {
    String value = WidgetLCAUtil.readPropertyValue( button, PARAM_SELECTION );
    if( value != null ) {
      button.setSelection( Boolean.valueOf( value ).booleanValue() );
    }
  }
  
  static void preserveValues( final Button button ) {
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( Props.TEXT, button.getText() );
    adapter.preserve( Props.IMAGE, button.getImage() );
    adapter.preserve( PROP_SELECTION, 
                      Boolean.valueOf( button.getSelection() ) );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( button ) ) );
    adapter.preserve( PROP_ALIGNMENT, new Integer( button.getAlignment() ) );
  }
  
  static void writeText( final Button button ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( button );
    
    if( WidgetLCAUtil.hasChanged( button, Props.TEXT, button.getText(), "" ) ) {
      writer.set( JSConst.QX_FIELD_LABEL, processMnemonics( button.getText() ) );      
    }
  }

  static void writeImage( final Button button ) throws IOException {
    Image image = button.getImage();
    if( WidgetLCAUtil.hasChanged( button, Props.IMAGE, image, null ) ) {
      String imagePath;
      if( image == null ) {
        imagePath = "";
      } else {
        imagePath = Image.getPath( image );
      }
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.set( JSConst.QX_FIELD_ICON, imagePath );
    }
  }

  static void writeAlignment( final Button button ) throws IOException {
    if( ( button.getStyle() & RWT.ARROW ) == 0 ) {
      Integer newValue = new Integer( button.getAlignment() );
      Integer defValue = DEFAULT_ALIGNMENT;
      if( WidgetLCAUtil.hasChanged( button, PROP_ALIGNMENT, newValue, defValue ) ) 
      {
        JSWriter writer = JSWriter.getWriterFor( button );
        String value;
        switch( newValue.intValue() ) {
          case RWT.LEFT:
            value = "left";
          break;
          case RWT.CENTER:
            value = "center";
          break;
          case RWT.RIGHT:
            value = "right";
          break;
          default:
            value = "left";
          break;
        }
        writer.set( "horizontalChildrenAlign", value );
      }
    }
  }

  static void writeSelection( final Button button ) throws IOException {
    Boolean newValue = Boolean.valueOf( button.getSelection() ); 
    Boolean defValue = Boolean.FALSE;
    JSWriter writer = JSWriter.getWriterFor( button );
    writer.set( PROP_SELECTION, JSConst.QX_FIELD_CHECKED, newValue, defValue );
  }
  
  private static String processMnemonics( String input ) {
    // TODO [rst] Change replacement to "$1<u>$2</u>" when we support
    // client-side mnemonics
    return input.replaceAll( "(^|[^&])&(\\p{Alnum})", "$1$2" )
      .replaceAll( "&&", "&" );
  }
}
