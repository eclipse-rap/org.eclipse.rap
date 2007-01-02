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
import org.eclipse.rap.rwt.internal.widgets.IWidgetAdapter;
import org.eclipse.rap.rwt.lifecycle.JSWriter;
import org.eclipse.rap.rwt.lifecycle.WidgetUtil;
import org.eclipse.rap.rwt.widgets.Button;

abstract class ButtonDelegateLCA {

  private static final Integer DEFAULT_ALIGNMENT = new Integer( RWT.CENTER );
  private static final String PROP_ALIGNMENT = "alignment";

  abstract void readData( Button button );
  abstract void renderInitialization( Button button ) throws IOException;
  abstract void renderChanges( Button button ) throws IOException;
  
  static void preserveAlignment( final Button button ) {
    IWidgetAdapter adapter = WidgetUtil.getAdapter( button );
    adapter.preserve( PROP_ALIGNMENT, new Integer( button.getAlignment() ) );
  }

  static void writeAlignment( final Button button ) throws IOException {
    if( ( button.getStyle() & RWT.ARROW ) == 0 ) {
      Integer newValue = new Integer( button.getAlignment() );
      Integer defValue = DEFAULT_ALIGNMENT;
      if( WidgetUtil.hasChanged( button, PROP_ALIGNMENT, newValue, defValue ) ) 
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
}
