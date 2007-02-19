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
import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.widgets.Text;
import org.eclipse.rap.rwt.widgets.Widget;

public class TextLCA extends AbstractWidgetLCA {

  private final static AbstractTextDelegateLCA SINGLE 
    = new SingleTextDelegateLCA();
  private final static AbstractTextDelegateLCA PASSWORD 
    = new PasswordTextDelegateLCA();
  private final static AbstractTextDelegateLCA MULTI 
    = new MultiTextDelegateLCA();

  public void preserveValues( final Widget widget ) {
    getLCADelegate( widget ).preserveValues( ( Text )widget );
  }

  public void readData( final Widget widget ) {
    getLCADelegate( widget ).readData( ( Text )widget );
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

  private static AbstractTextDelegateLCA getLCADelegate( final Widget widget ) {
    AbstractTextDelegateLCA result;
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
