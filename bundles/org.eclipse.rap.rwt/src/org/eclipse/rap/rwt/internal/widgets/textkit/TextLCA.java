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

  public void preserveValues( final Widget widget ) {
    Text text = ( Text )widget;
    ControlLCAUtil.preserveValues( text );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.TEXT, getRenderText( text ) );
  }

  public void readData( final Widget widget ) {
    String newText = WidgetUtil.readPropertyValue( widget, "text" );
    if( newText != null ) {
      Text text = ( Text )widget;
      text.setText( newText );
    }
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( ( Text )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderChanges( ( Text )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }

  /**
   * Returns the text to be rendered for the <code>Text</code>. For single-line
   * text widgets, newlines are replaced by white spaces.
   * @param text the Text control in question.
   * @return The text to be rendered. 
   */
  private String getRenderText( final Text text ) {
    String result = text.getText();
    if( ( text.getStyle() & RWT.SINGLE ) != 0 ) {
      result = result.replaceAll( "\n", " " );
    }
    return result;
  }

  private static TextDelegateLCA getLCADelegate( final Widget widget ) {
    TextDelegateLCA result;
    int style = ( ( Text )widget ).getStyle();
    if( ( style & RWT.PASSWORD ) != 0 ) {
      result = PASSWORD;
    } else if( ( style & RWT.SINGLE ) != 0 ) {
      result = SINGLE;
    } else if( ( style & RWT.MULTI ) != 0 ) {
      result = MULTI;
    } else {
      result = SINGLE;
    }
    return result;
  }
}
