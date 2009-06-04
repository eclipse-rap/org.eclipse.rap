/*******************************************************************************
 * Copyright (c) 2002, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets.buttonkit;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.rwt.lifecycle.AbstractWidgetLCA;
import org.eclipse.rwt.lifecycle.JSWriter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;


public final class ButtonLCA extends AbstractWidgetLCA {
  
  private final static Object[] NULL_PARAMETER = new Object[] { null };
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
    Button button = ( Button )widget;
    Shell shell = button.getShell();
    if( !shell.isDisposed() && ButtonLCAUtil.isDefaultButton( button ) ) {
      JSWriter writer = JSWriter.getWriterFor( button );
      writer.call( shell, "setDefaultButton", NULL_PARAMETER );
    }
    getLCADelegate( widget ).renderDispose( ( Button )widget );
  }

  public void createResetHandlerCalls( final String typePoolId ) 
    throws IOException 
  {
    getLCADelegate( typePoolId ).createResetHandlerCalls( typePoolId );
  }

  public String getTypePoolId( final Widget widget ) {
    return getLCADelegate( widget ).getTypePoolId( ( Button )widget );
  }

  private static ButtonDelegateLCA getLCADelegate( final String tpId ) {
    ButtonDelegateLCA result;
    if( tpId.startsWith( CheckButtonDelegateLCA.TYPE_POOL_ID ) ) {
      result = CHECK;
    } else if( tpId.startsWith( PushButtonDelegateLCA.TYPE_POOL_ID ) ) {
      result = PUSH;
    } else if( tpId.startsWith( RadioButtonDelegateLCA.TYPE_POOL_ID ) ) {
      result = RADIO;
    } else {
      String txt= "The typePoolId ''{0}'' is not supported.";
      String msg = MessageFormat.format( txt, new Object[] { tpId } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }

  private static ButtonDelegateLCA getLCADelegate( final Widget widget ) {
    ButtonDelegateLCA result;
    int style = ( ( Button )widget ).getStyle();
    if( ( style & SWT.CHECK ) != 0 ) {
      result = CHECK;
    } else if( ( style & SWT.PUSH ) != 0 ) {
      result = PUSH;
    } else if( ( style & SWT.RADIO ) != 0 ) {
      result = RADIO;
    } else {
      result = PUSH;
    }
    return result;
  }
}
