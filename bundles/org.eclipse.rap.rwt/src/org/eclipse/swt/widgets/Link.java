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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.internal.graphics.FontSizeEstimation;
import org.eclipse.swt.internal.widgets.ILinkAdapter;

public class Link extends Control {

  private String text = "";
  private String displayText = "";
  private Point[] offsets;
  private String[] ids;
  private int[] mnemonics;
  private ILinkAdapter linkAdapter;

  public Link( final Composite parent, final int style ) {
    super( parent, style );
  }

  public void setText( final String string ) {
    checkWidget();
    if( string == null ) {
      SWT.error( SWT.ERROR_NULL_ARGUMENT );
    }
    if( !string.equals( text ) ) {
      displayText = parse( string );
      text = string;
    }
  }

  public String getText() {
    checkWidget();
    return text;
  }

  ///////////////////////////////////////
  // Listener registration/deregistration
  
  public void addSelectionListener( final SelectionListener listener ) {
    SelectionEvent.addListener( this, listener );
  }

  public void removeSelectionListener( final SelectionListener listener ) {
    SelectionEvent.removeListener( this, listener );
  }

  public Point computeSize( int wHint, int hHint, boolean changed ) {
    checkWidget();
    int width = 0;
    int height = 0;
    int border = getBorderWidth();
    if( ( displayText.length() > 0 ) ) {
      // TODO [rst] change to textExtent when wrap supported
      Point extent = FontSizeEstimation.stringExtent( displayText, getFont() );
      width = extent.x + 8;
      height = extent.y + 2;
    }
    if( wHint != SWT.DEFAULT ) {
      width = wHint;
    }
    if( hHint != SWT.DEFAULT ) {
      height = hHint;
    }
    width += border * 2;
    height += border * 2;
    return new Point( width, height );
  }
  
  public int getBorderWidth() {
    return ( ( style & SWT.BORDER ) != 0 ) ? 1 : 0;
  }
  
  public Object getAdapter( final Class adapter ) {
    Object result;
    if( adapter == ILinkAdapter.class ) {
      if( linkAdapter == null ) {
        linkAdapter = new ILinkAdapter() {
          public String getDisplayText() {
            return displayText;
          }
          public Point[] getOffsets() {
            return offsets;
          }
          public String[] getIds() {
            return ids;
          }
        };
      }
      result = linkAdapter;
    } else {
      result = super.getAdapter( adapter );
    }
    return result;
  }
  
  boolean isTabGroup() {
    return true;
  }

  /* verbatim copy from SWT */
  String parse( String string ) {
    int length = string.length();
    offsets = new Point[ length / 4 ];
    ids = new String[ length / 4 ];
    mnemonics = new int[ length / 4 + 1 ];
    StringBuffer result = new StringBuffer();
    char[] buffer = new char[ length ];
    string.getChars( 0, string.length(), buffer, 0 );
    int index = 0, state = 0, linkIndex = 0;
    int start = 0, tagStart = 0, linkStart = 0, endtagStart = 0, refStart = 0;
    while( index < length ) {
      char c = Character.toLowerCase( buffer[ index ] );
      switch( state ) {
        case 0:
          if( c == '<' ) {
            tagStart = index;
            state++;
          }
        break;
        case 1:
          if( c == 'a' )
            state++;
        break;
        case 2:
          switch( c ) {
            case 'h':
              state = 7;
            break;
            case '>':
              linkStart = index + 1;
              state++;
            break;
            default:
              if( Character.isWhitespace( c ) )
                break;
              else
                state = 13;
          }
        break;
        case 3:
          if( c == '<' ) {
            endtagStart = index;
            state++;
          }
        break;
        case 4:
          state = c == '/'
                          ? state + 1
                          : 3;
        break;
        case 5:
          state = c == 'a'
                          ? state + 1
                          : 3;
        break;
        case 6:
          if( c == '>' ) {
            mnemonics[ linkIndex ] = parseMnemonics( buffer,
                                                     start,
                                                     tagStart,
                                                     result );
            int offset = result.length();
            parseMnemonics( buffer, linkStart, endtagStart, result );
            offsets[ linkIndex ] = new Point( offset, result.length() - 1 );
            if( ids[ linkIndex ] == null ) {
              ids[ linkIndex ] = new String( buffer, linkStart, endtagStart
                                                                - linkStart );
            }
            linkIndex++;
            start = tagStart = linkStart = endtagStart = refStart = index + 1;
            state = 0;
          } else {
            state = 3;
          }
        break;
        case 7:
          state = c == 'r'
                          ? state + 1
                          : 0;
        break;
        case 8:
          state = c == 'e'
                          ? state + 1
                          : 0;
        break;
        case 9:
          state = c == 'f'
                          ? state + 1
                          : 0;
        break;
        case 10:
          state = c == '='
                          ? state + 1
                          : 0;
        break;
        case 11:
          if( c == '"' ) {
            state++;
            refStart = index + 1;
          } else {
            state = 0;
          }
        break;
        case 12:
          if( c == '"' ) {
            ids[ linkIndex ] = new String( buffer, refStart, index - refStart );
            state = 2;
          }
        break;
        case 13:
          if( Character.isWhitespace( c ) ) {
            state = 0;
          } else if( c == '=' ) {
            state++;
          }
        break;
        case 14:
          state = c == '"'
                          ? state + 1
                          : 0;
        break;
        case 15:
          if( c == '"' )
            state = 2;
        break;
        default:
          state = 0;
        break;
      }
      index++;
    }
    if( start < length ) {
      int tmp = parseMnemonics( buffer, start, tagStart, result );
      int mnemonic = parseMnemonics( buffer, linkStart, index, result );
      if( mnemonic == -1 )
        mnemonic = tmp;
      mnemonics[ linkIndex ] = mnemonic;
    } else {
      mnemonics[ linkIndex ] = -1;
    }
    if( offsets.length != linkIndex ) {
      Point[] newOffsets = new Point[ linkIndex ];
      System.arraycopy( offsets, 0, newOffsets, 0, linkIndex );
      offsets = newOffsets;
      String[] newIDs = new String[ linkIndex ];
      System.arraycopy( ids, 0, newIDs, 0, linkIndex );
      ids = newIDs;
      int[] newMnemonics = new int[ linkIndex + 1 ];
      System.arraycopy( mnemonics, 0, newMnemonics, 0, linkIndex + 1 );
      mnemonics = newMnemonics;
    }
    return result.toString();
  }
  
  /* verbatim copy from SWT */
  int parseMnemonics( char[] buffer, int start, int end, StringBuffer result ) {
    int mnemonic = -1, index = start;
    while( index < end ) {
      if( buffer[ index ] == '&' ) {
        if( index + 1 < end && buffer[ index + 1 ] == '&' ) {
          result.append( buffer[ index ] );
          index++;
        } else {
          mnemonic = result.length();
        }
      } else {
        result.append( buffer[ index ] );
      }
      index++;
    }
    return mnemonic;
  }
}
