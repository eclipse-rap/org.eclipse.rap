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

package org.eclipse.rap.rwt.widgets;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.Rectangle;
import org.eclipse.rap.rwt.internal.graphics.FontSizeEstimation;

public class TableItem extends Item {

  private final Table parent;
  private String[] texts;

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
  
  public void setText( final int index, final String text ) {
    checkWidget();
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( index == 0 ) {
      super.setText( text );
    } else {
      int count = Math.max( 1, parent.getColumnCount() );
      if( index > 0 && index < count ) {
        if( texts == null ) {
          texts = new String[ count ];
          texts[ 0 ] = getText();
        } else if( texts.length < count ) {
          enlargeTexts( count );
        }
        texts[ index ] = text;
      }
    }
  }

  public String getText( final int index ) {
    checkWidget();
    String result = "";
    if( index == 0 ) {
      result = super.getText();
    } else {
      int count = Math.max( 1, parent.getColumnCount() );
      if( texts != null && index > 0 && index < count ) {
        if( texts.length < count ) {
          enlargeTexts( count );
        }
        result = texts[ index ];
        if( result == null ) {
          result = "";
        }
      }
    }
    return result;
  }

  final void removeText( final int index ) {
    if( texts != null ) {
      String[] newTexts = new String[ texts.length - 1 ];
      System.arraycopy( texts, 0, newTexts, 0, index );
      int offSet = texts.length - index - 1;
      System.arraycopy( texts, index + 1, newTexts, index, offSet );
      texts = newTexts;
      if( index == 0 ) {
        if( texts.length == 0 ) {
          super.setText( "" );
        } else {
          super.setText( texts[ 0 ] );
        }
      }
    }
  }
  
  public void clear() {
    checkWidget();
    super.setText( "" );
    texts = null;
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
  
  private void enlargeTexts( final int count ) {
    String[] newTexts = new String[ count ];
    System.arraycopy( texts, 0, newTexts, 0, texts.length );
    texts = newTexts;
  }

  private static Table checkNull( final Table table ) {
    if( table == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return table;
  }
}