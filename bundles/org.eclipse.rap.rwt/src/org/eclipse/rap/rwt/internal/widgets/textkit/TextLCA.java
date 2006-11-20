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

package org.eclipse.rap.rwt.internal.widgets.textkit;

import java.io.IOException;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.widgets.*;
import org.eclipse.rap.rwt.lifecycle.*;
import org.eclipse.rap.rwt.widgets.Text;
import org.eclipse.rap.rwt.widgets.Widget;

public class TextLCA extends AbstractWidgetLCA {

  private final static TextDelegateLCA SINGLE = new SingleTextDelegateLCA();
  private final static TextDelegateLCA PASSWORD = new PasswordTextDelegateLCA();
  private final static TextDelegateLCA MULTI = new MultiTextDelegateLCA();
  private final static TextDelegateLCA WRAP = new WrapTextDelegateLCA();

  private static TextDelegateLCA getLCADelegate( final Widget widget ) {
    TextDelegateLCA result;
    int style = ( ( Text )widget ).getStyle();
    if( ( style & RWT.PASSWORD ) != 0 ) {
      result = PASSWORD;
    } else if( ( style & RWT.SINGLE ) != 0 ) {
      result = SINGLE;
    } else if( ( style & RWT.WRAP ) != 0 ) {
      result = WRAP;
    } else if( ( style & RWT.MULTI ) != 0 ) {
      result = MULTI;
    } else {
      result = SINGLE;
    }
    return result;
  }

  public void preserveValues( final Widget widget ) {
    Text text = ( Text )widget;
    ControlLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.TEXT, text.getText() );
  }

  public void readData( final Widget widget ) {
    String newText = WidgetUtil.readPropertyValue( widget, "text" );
    if( newText != null ) {
      Text text = ( Text )widget;
      text.setText( newText );
    }
  }

  public void processAction( final Widget widget ) {
  }

  public void renderInitialization( Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    Text text = ( Text )widget;
    JSWriter writer = JSWriter.getWriterFor( widget );
    ControlLCAUtil.writeBounds( text );
    ControlLCAUtil.writeToolTip( text );
    ControlLCAUtil.setControlIntoToolItem( text );
    writer.set( Props.TEXT, "value", text.getText() );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
