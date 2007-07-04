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

package org.eclipse.swt.internal.widgets.textkit;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.lifecycle.AbstractWidgetLCA;
import org.eclipse.swt.lifecycle.JSWriter;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public final class TextLCA extends AbstractWidgetLCA {

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

  public void createResetHandlerCalls( final String typePoolId ) throws IOException {
    getLCADelegate( typePoolId ).createResetHandlerCalls( typePoolId );
  }
  
  public String getTypePoolId( final Widget widget ) throws IOException {
//    return getLCADelegate( widget ).getTypePoolId( ( Text )widget );
    return null;
  }
  
  private static AbstractTextDelegateLCA getLCADelegate( final String tpId ) {
    AbstractTextDelegateLCA result;
    if( tpId.startsWith( PasswordTextDelegateLCA.PREFIX_TYPE_POOL_ID ) ) {
      result = PASSWORD;
    } else if( tpId.startsWith( SingleTextDelegateLCA.PREFIX_TYPE_POOL_ID ) ) {
      result = SINGLE;
    } else if( tpId.startsWith( MultiTextDelegateLCA.PREFIX_TYPE_POOL_ID ) ) {
      result = MULTI;
    } else {
      String txt= "The typePoolId ''{0}'' is not supported.";
      String msg = MessageFormat.format( txt, new Object[] { tpId } );
      throw new IllegalArgumentException( msg );
    }
    return result;
  }
  
  private static AbstractTextDelegateLCA getLCADelegate( final Widget widget ) {
    AbstractTextDelegateLCA result;
    int style = ( ( Text )widget ).getStyle();
    if( ( style & SWT.PASSWORD ) != 0 ) {
      result = PASSWORD;
    } else if( ( style & SWT.SINGLE ) != 0 ) {
      result = SINGLE;
    } else if( ( style & SWT.MULTI ) != 0 ) {
      result = MULTI;
    } else {
      result = SINGLE;
    }
    return result;
  }
}
