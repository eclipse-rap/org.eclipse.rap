/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.toolitemkit;

import java.io.IOException;

import org.eclipse.rap.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;


public final class ToolItemLCA extends AbstractWidgetLCA {

  private final static ToolItemDelegateLCA PUSH = new PushToolItemLCA();
  private final static ToolItemDelegateLCA CHECK = new CheckToolItemLCA();
  private final static ToolItemDelegateLCA RADIO = new RadioToolItemLCA();
  private final static ToolItemDelegateLCA SEPERATOR = new SeparatorToolItemLCA();
  private final static ToolItemDelegateLCA DROP_DOWN = new DropDownToolItemLCA();

  public void preserveValues( Widget widget ) {
    getLCADelegate( widget ).preserveValues( ( ToolItem )widget );
  }

  public void readData( Widget widget ) {
    getLCADelegate( widget ).readData( ( ToolItem )widget );
  }

  public void renderInitialization( Widget widget ) throws IOException {
    getLCADelegate( widget ).renderInitialization( ( ToolItem )widget );
  }

  public void renderChanges( Widget widget ) throws IOException {
    getLCADelegate( widget ).renderChanges( ( ToolItem )widget );
  }

  private static ToolItemDelegateLCA getLCADelegate( Widget widget ) {
    ToolItemDelegateLCA result;
    int style = ( ( ToolItem )widget ).getStyle();
    if( ( style & SWT.CHECK ) != 0 ) {
      result = CHECK;
    } else if( ( style & SWT.PUSH ) != 0 ) {
      result = PUSH;
    } else if( ( style & SWT.SEPARATOR ) != 0 ) {
      result = SEPERATOR;
    } else if( ( style & SWT.DROP_DOWN ) != 0 ) {
      result = DROP_DOWN;
    } else if( ( style & SWT.RADIO ) != 0 ) {
      result = RADIO;
    } else {
      result = PUSH;
    }
    return result;
  }
}
