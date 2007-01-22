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
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.widgets.Button;
import org.eclipse.rap.rwt.widgets.Widget;


public final class ButtonLCA extends AbstractWidgetLCA {
  
  private final static ButtonDelegateLCA PUSH = new PushButtonDelegateLCA();
  private final static ButtonDelegateLCA CHECK = new CheckButtonDelegateLCA();
  private final static ButtonDelegateLCA RADIO = new RadioButtonDelegateLCA();
  
  public void preserveValues( final Widget widget ) {
    getLCADelegate( widget ).preserveValues( ( Button )widget );
  }

  public void readData( final Widget widget ) {
    getLCADelegate( widget ).readData( ( Button )widget );
  }

  public void renderInitialization( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( ( Button )widget );
  }

  public void renderChanges( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderChanges( ( Button )widget );
  }

  public void renderDispose( final Widget widget ) throws IOException {
    getLCADelegate( widget ).renderDispose( ( Button )widget );
  }

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
}
