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

package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;

public class TableItem extends Item {

  private static final class Data {
    String text;
  }
  
  private final Table parent;
  private Data[] data;

  public TableItem( final Table parent, final int style ) {
    this( parent, style, checkNull( parent).getItemCount() );
  }

  public TableItem( final Table parent, final int style, final int index ) {
    super( parent, style );
    this.parent = parent;
    this.parent.createItem( this, index );
  }
  
  public Display getDisplay() {
    checkWidget();
    return parent.getDisplay();
  }

  public Table getParent() {
    checkWidget();
    return parent;
  }
  
  //////////////////////////////////////////////////////////
  // Methods to get/set text for second column and following  
  
  public void setText( final String text ) {
    checkWidget();
    setText( 0, text );
  }
  
  public void setText( final int index, final String text ) {
    checkWidget();
    if( text == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    int count = Math.max( 1, parent.getColumnCount() );
    if( index >= 0 && index < count ) {
      if( data == null ) {
        data = new Data[ count ];
      } else if( data.length < count ) {
        enlargeData( count );
      }
      if( data[ index ] == null ) {
        data[ index ] = new Data();
      }
      data[ index ].text = text;
    }
  }

  public String getText() {
    checkWidget();
    return getText( 0 );
  }
  
  public String getText( final int index ) {
    checkWidget();
    String result = "";
    if(    data != null 
        && index >= 0 
        && index < data.length 
        && data[ index ] != null ) 
    {
      result = data[ index ].text;
    }
    return result;
  }

  final void removeText( final int index ) {
    if( data != null && parent.getColumnCount() > 1 ) {
      Data[] newData = new Data[ data.length - 1 ];
      System.arraycopy( data, 0, newData, 0, index );
      int offSet = data.length - index - 1;
      System.arraycopy( data, index + 1, newData, index, offSet );
      data = newData;
    }
  }
  
  public void clear() {
    checkWidget();
    data = null;
    super.setImage( null );
  }

  /////////////////////
  // Dimension methods
  
  public Rectangle getBounds() {
    return getBounds( 0 );
  }
  
  // TODO [rh] could be optimized by storing values for left and top position
  public Rectangle getBounds( final int index ) {
    checkWidget();
//  if (!parent.checkData (this, true)) error (SWT.ERROR_WIDGET_DISPOSED);
    Rectangle result;
    int itemIndex = parent.indexOf( this );
    if( index == 0 && parent.getColumnCount() == 0 ) {
      Font font = parent.getFont();
      int width = FontSizeEstimation.stringExtent( getText(), font ).x;
      result = new Rectangle( 0, 0, width, getHeight() );
    } else {
      if( itemIndex != -1 && index < parent.getColumnCount() ) {
        int left = 0;
        for( int i = 0; i < index; i++ ) {
          left += parent.getColumn( i ).getWidth();
        }
        int top = 0; 
        for( int i = 0; i < itemIndex; i++ ) {
          top += parent.getItem( i ).getHeight();
        }
        int width = parent.getColumn( index ).getWidth();
        result = new Rectangle( left, top, width, getHeight() );
      } else {
        result = new Rectangle( 0, 0, 0, 0 );
      }
    }
    return result;
  }
  
  final int getHeight() {
    // TODO [rh] replace with this.getFont() once TableItem supports fonts
    // TODO [rh] preliminary: this is only an approximation for item height
    return FontSizeEstimation.getCharHeight( parent.getFont() ) + 4;
  }

  ///////////////////////////////
  // helping methods for disposal

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    parent.destroyItem( this );
  }

  protected void releaseWidget() {
  }
  
  //////////////////
  // helping methods
  
  private void enlargeData( final int count ) {
    Data[] newData = new Data[ count ];
    System.arraycopy( data, 0, newData, 0, data.length );
    data = newData;
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}