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
import org.eclipse.rap.rwt.internal.widgets.ItemHolder;

public class TableItem extends Item {

  private final Table parent;
  private String[] texts;

  public TableItem( final Table parent, final int style ) {
    super( parent, style );
    this.parent = parent;
    ItemHolder.addItem( parent, this );
  }

  public Display getDisplay() {
    return parent.getDisplay();
  }

  public void setText( final int index, final String text ) {
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

  void removeText( int index ) {
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
  

  ///////////////////////////////
  // helping methods for disposal

  protected void releaseChildren() {
  }

  protected void releaseParent() {
    ItemHolder.removeItem( parent, this );
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
}