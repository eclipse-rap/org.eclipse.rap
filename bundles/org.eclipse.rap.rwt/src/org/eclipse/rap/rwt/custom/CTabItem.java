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

package org.eclipse.rap.rwt.custom;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.graphics.*;
import org.eclipse.rap.rwt.widgets.*;


public class CTabItem extends Item {

  static final int TOP_MARGIN = 2;
  static final int BOTTOM_MARGIN = 2;
  static final int LEFT_MARGIN = 4;
  static final int RIGHT_MARGIN = 4;
  static final int INTERNAL_SPACING = 4;
  
  static final String ELLIPSIS = "..."; 
  
  private final CTabFolder parent;
  private Control control;
  private String toolTipText;
  private Font font;
  // TODO [rh] shortenedText is not yet calculated
  String shortenedText;
  int shortenedTextWidth;
  
  boolean showing = false;
  int x;
  int y;
  int width;
  int height;
  
  public CTabItem( final CTabFolder parent, final int style ) {
    this( checkParent( parent ), style, parent.getItemCount() );
  }

  public CTabItem( final CTabFolder parent, final int style, final int index ) {
    super( parent, checkStyle( style ) );
    this.parent = parent;
    parent.createItem( this, index );
  }
  
  public Display getDisplay() {
    return parent.getDisplay();
  }
  
  public CTabFolder getParent() {
    return parent;
  }
  
  /////////////////////////////////////////
  // Getter/setter for principal properties
  
  public void setText( final String text ) {
    if( text == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    if( !text.equals( getText() ) ) {
      super.setText( text );
      shortenedText = null;
      shortenedTextWidth = 0;
      parent.updateItems();
    }
  }
  
  public void setImage( final Image image ) {
    if( image != getImage() ) {
      super.setImage( image );
      parent.updateItems();
    }
  }

  /** <p>Changing font works, but tab size is not adjusted accordingly.</p> */
  public void setFont( final Font font ) {
    checkWidget();
    if( font != this.font ) {
      this.font = font;
      if( !parent.updateTabHeight( false ) ) {
        parent.updateItems();
      }
    }
  }

  public Font getFont() {
    checkWidget();
    if( font != null )
      return font;
    return parent.getFont();
  }

  public Control getControl() {
    return control;
  }

  public void setControl( final Control control ) {
    if( control != null ) {
      if( control.isDisposed() ) {
        RWT.error( RWT.ERROR_INVALID_ARGUMENT );
      }
      if( control.getParent() != parent ) {
        RWT.error( RWT.ERROR_INVALID_PARENT );
      }
    }
    if( this.control != null && !this.control.isDisposed() ) {
      this.control.setVisible( false );
    }
    this.control = control;
    if( this.control != null ) {
      int index = parent.indexOf( this );
      if( index == parent.getSelectionIndex() ) {
        this.control.setBounds( parent.getClientArea() );
        this.control.setVisible( true );
      } else {
        this.control.setVisible( false );
      }
    }
  }
  
  public void setToolTipText( final String toolTipText ) {
    this.toolTipText = toolTipText;
  }
  
  public String getToolTipText () {
    String result = toolTipText;
    if( result == null && shortenedText != null ) {
      String text = getText();
      if( !shortenedText.equals( text ) ) {
        result = text;
      }
    }
    return result;
  }
  
  ////////////////////////
  // Bounds and visibility
  
  public boolean isShowing () {
    return showing;
  }
  
  public Rectangle getBounds() {
    return new Rectangle( x, y, width, height );
  }
  
  ///////////////////
  // Widget overrides

  protected void releaseChildren() {
    // do nothing
  }

  protected void releaseParent() {
    parent.destroyItem( this );
  }

  protected void releaseWidget() {
    // do nothing
  }
  
  ///////////////////////////////////////////////////////////////////////
  // Helping methods used by CTabFolder to control item size and location

  int preferredHeight() {
    return 20;
  }
  
  int preferredWidth( final boolean isSelected, final boolean minimum ) {
    int result = 0;
    if( !isDisposed() ) {
      int width = 0;
      Image image = getImage();
      if( image != null ) {
        // TODO [rh] determine bounds for image
//        width += image.getBounds().width;
        width += 20;
      }
      String text = null;
      if( minimum ) {
        int minChars = parent.getMinimumCharacters();
        text = minChars == 0 ? null : getText();
        if( text != null && text.length() > minChars ) {
          int end = minChars < ELLIPSIS.length() + 1 
                  ? minChars 
                  : minChars - ELLIPSIS.length();
          text = text.substring( 0, end );
          if( minChars > ELLIPSIS.length() + 1 ) {
            text += ELLIPSIS;
          }
        }
      } else {
        text = getText();
      }
      if( text != null ) {
        if( width > 0 ) {
          width += INTERNAL_SPACING;
        }
        // TODO [rh] fake calculation of text size...
        int charWidth = 4;
        if( ( getFont().getStyle() & RWT.BOLD ) != 0 ) {
          charWidth++;
        }
        width += text.length() * charWidth; 
      }
      if( canClose() && ( isSelected || parent.getUnselectedCloseVisible() ) ) {
        if( result > 0 ) {
          result += INTERNAL_SPACING;
        }
        result += CTabFolder.BUTTON_SIZE;
      }
      result = width + LEFT_MARGIN + RIGHT_MARGIN;
    }
    return result;
  }

  boolean canClose() {
    return   ( parent.getStyle() & RWT.CLOSE ) != 0 
          || ( getStyle() & RWT.CLOSE ) != 0;
  }

  //////////////////
  // Helping methods
  
  private static CTabFolder checkParent( final CTabFolder parent ) {
    if( parent == null ) {
      RWT.error( RWT.ERROR_NULL_ARGUMENT );
    }
    return parent;
  }

  private static int checkStyle( final int style ) {
    int result = RWT.NONE;
    if( ( style & RWT.CLOSE ) != 0 ) {
      result = RWT.CLOSE;
    }
    return result;
  }
}
