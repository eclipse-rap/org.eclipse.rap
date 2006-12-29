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
import org.eclipse.rap.rwt.widgets.Widget;


public class ButtonLCA extends AbstractWidgetLCA {
  
  private final static ButtonDelegateLCA PUSH = new PushButtonDelegateLCA();
  private final static ButtonDelegateLCA CHECK = new CheckButtonDelegateLCA();
  private final static ButtonDelegateLCA RADIO = new RadioButtonDelegateLCA();
  
  private static ButtonDelegateLCA getLCADelegate( final Widget widget ) {
    ButtonDelegateLCA result;
    int style = ( ( Button )widget ).getStyle();
    if( ( style & RWT.CHECK ) != 0 ) {
      result = CHECK;
    } else if( ( style & RWT.PUSH ) != 0 ) {
      result = PUSH;
    } else if( ( style & RWT.RADIO ) != 0 ) {
      result = RADIO;
    } else {
      result = PUSH;
    }
    return result;
  }

  public void preserveValues( final Widget widget ) {
    Button button = ( Button )widget;
    ControlLCAUtil.preserveValues( button );
    IWidgetAdapter adapter = WidgetUtil.getAdapter( widget );
    adapter.preserve( Props.TEXT, button.getText() );
    adapter.preserve( Props.IMAGE, Image.getPath( button.getImage() ) );
    adapter.preserve( Props.SELECTION_LISTENERS,
                      Boolean.valueOf( SelectionEvent.hasListener( button ) ) );
  }

  public void readData( final Widget widget ) {
    getLCADelegate( widget ).readData( widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderChanges( widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    JSWriter writer = JSWriter.getWriterFor( widget );
    writer.dispose();
  }
}
