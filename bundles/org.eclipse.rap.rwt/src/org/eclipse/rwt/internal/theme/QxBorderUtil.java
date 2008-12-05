/*******************************************************************************
 * Copyright (c) 2008 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;


public final class QxBorderUtil {

  private static final String[] DARKSHADOW_LIGHTSHADOW
    = new String[] { "widget.darkshadow", "widget.lightshadow" };
  private static final String[] LIGHTSHADOW_DARKSHADOW
    = new String[] { "widget.lightshadow", "widget.darkshadow" };
  private static final String[] SHADOW_HIGHLIGHT
    = new String[] { "widget.shadow", "widget.highlight" };
  private static final String[] HIGHLIGHT_SHADOW
    = new String[] { "widget.highlight", "widget.shadow" };

  private QxBorderUtil() {
    // prevent instantiation
  }

  public static JsonArray getColors( final QxBorder border,
                                     final Theme theme )
  {
    JsonArray result = null;
    if( border.color == null && border.width == 2 ) {
      if( "outset".equals( border.style ) ) {
        result = getBorderColors( LIGHTSHADOW_DARKSHADOW, theme );
      } else if( "inset".equals( border.style ) ) {
        result = getBorderColors( SHADOW_HIGHLIGHT, theme );
      } else if( "ridge".equals( border.style ) ) {
        result = getBorderColors( HIGHLIGHT_SHADOW, theme );
      } else if( "groove".equals( border.style ) ) {
        result = getBorderColors( SHADOW_HIGHLIGHT, theme );
      }
    } else if( border.color == null && border.width == 1 ) {
      if( "outset".equals( border.style ) ) {
        result = getBorderColors( HIGHLIGHT_SHADOW, theme );
      } else if( "inset".equals( border.style ) ) {
        result = getBorderColors( SHADOW_HIGHLIGHT, theme );
      }
    } else if( border.color != null ) {
      result = new JsonArray();
      result.append( border.color );
    }
    return result;
  }

  public static JsonArray getInnerColors( final QxBorder border,
                                            final Theme theme )
  {
    JsonArray result = null;
    if( border.color == null && border.width == 2 ) {
      if( "outset".equals( border.style ) ) {
        result = getBorderColors( HIGHLIGHT_SHADOW, theme );
      } else if( "inset".equals( border.style ) ) {
        result = getBorderColors( DARKSHADOW_LIGHTSHADOW, theme );
      } else if( "ridge".equals( border.style ) ) {
        result = getBorderColors( SHADOW_HIGHLIGHT, theme );
      } else if( "groove".equals( border.style ) ) {
        result = getBorderColors( HIGHLIGHT_SHADOW, theme );
      }
    }
    return result;
  }

  private static JsonArray getBorderColors( final String[] properties,
                                            final Theme theme )
  {
    QxColor color1 = ( QxColor )theme.getValue( properties[ 0 ] );
    QxColor color2 = ( QxColor )theme.getValue( properties[ 1 ] );
    String name1 = Theme.createCssPropertyName( color1 );
    String name2 = Theme.createCssPropertyName( color2 );
    JsonArray array = new JsonArray();
    array.append( name1 );
    array.append( name2 );
    array.append( name2 );
    array.append( name1 );
    return array;
  }
}
