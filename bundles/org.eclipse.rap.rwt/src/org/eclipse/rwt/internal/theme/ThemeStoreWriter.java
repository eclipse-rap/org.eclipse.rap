/*******************************************************************************
 * Copyright (c) 2009 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.*;

import org.eclipse.rwt.internal.theme.css.ConditionalValue;


public final class ThemeStoreWriter {

  private Set valueSet = new HashSet();
  private final IThemeCssElement[] elements;
  private StringBuffer cssValuesBuffer;

  public ThemeStoreWriter( final IThemeCssElement[] elements ) {
    this.elements = elements;
    cssValuesBuffer = new StringBuffer();
  }

  public void addTheme( final Theme theme, boolean isDefault ) {
    QxType[] values = theme.getValues();
    for( int i = 0; i < values.length; i++ ) {
      valueSet.add( values[ i ] );
    }
    createThemeStoreCss( theme, isDefault );
  }

  public String createJs() {
    StringBuffer buffer = new StringBuffer();
    buffer.append( "ts = org.eclipse.swt.theme.ThemeStore.getInstance();\n" );
    JsonObject valuesMap = createValues();
    buffer.append( "ts.defineValues(" + valuesMap + ");\n" );
    buffer.append( cssValuesBuffer );
    buffer.append( "delete ts;\n" );
    return buffer.toString();
  }

  private JsonObject createValues() {
    JsonObject dimensionMap = new JsonObject();
    JsonObject boxDimensionMap = new JsonObject();
    JsonObject imageMap = new JsonObject();
    JsonObject gradientMap = new JsonObject();
    JsonObject colorMap = new JsonObject();
    JsonObject fontMap = new JsonObject();
    JsonObject borderMap = new JsonObject();
    QxType[] values = new QxType[ valueSet.size() ];
    valueSet.toArray( values );
    for( int i = 0; i < values.length; i++ ) {
      QxType value = values[ i ];
      String key = Theme.createCssKey( value );
      if( value instanceof QxDimension ) {
        QxDimension dim = ( QxDimension )value;
        dimensionMap.append( key, dim.value );
      } else if( value instanceof QxBoxDimensions ) {
        QxBoxDimensions boxdim = ( QxBoxDimensions )value;
        JsonArray boxArray = new JsonArray();
        boxArray.append( boxdim.top );
        boxArray.append( boxdim.right );
        boxArray.append( boxdim.bottom );
        boxArray.append( boxdim.left );
        boxDimensionMap.append( key, boxArray );
      } else if( value instanceof QxImage ) {
        QxImage image = ( QxImage )value;
        if( image.none ) {
          if( image.gradientColors != null && image.gradientPercents != null ) {
            JsonObject gradientObject = new JsonObject();
            JsonArray percents = JsonArray.valueOf( image.gradientPercents );
            gradientObject.append( "percents", percents );
            JsonArray colors = JsonArray.valueOf( image.gradientColors );
            gradientObject.append( "colors", colors );
            gradientMap.append( key, gradientObject );
          }
          imageMap.append( key, JsonValue.NULL );
        } else {
          imageMap.append( key, key );
        }
      } else if( value instanceof QxColor ) {
        QxColor color = ( QxColor )value;
        if( color.transparent ) {
          colorMap.append( key, "undefined" );
        } else {
          colorMap.append( key, QxColor.toHtmlString( color.red,
                                                      color.green,
                                                      color.blue ) );
        }
      } else if( value instanceof QxFont && true ) {
        QxFont font = ( QxFont )value;
        JsonObject fontObject = new JsonObject();
        fontObject.append( "family", JsonArray.valueOf( font.family ) );
        fontObject.append( "size", font.size );
        fontObject.append( "bold", font.bold );
        fontObject.append( "italic", font.italic );
        fontMap.append( key, fontObject );
      } else if( value instanceof QxBorder && true ) {
        QxBorder border = ( QxBorder )value;
        JsonObject borderObject = new JsonObject();
        borderObject.append( "width", border.width );
        borderObject.append( "style", border.style );
        borderObject.append( "color", border.color );
        if( border.radius != null ) {
          JsonArray boxArray = new JsonArray();
          boxArray.append( border.radius.x );
          boxArray.append( border.radius.y );
          boxArray.append( border.radius.width );
          boxArray.append( border.radius.height );
          borderObject.append( "radius", boxArray );
        }
        borderMap.append( key, borderObject );
      }
    }
    JsonObject valuesMap = new JsonObject();
    valuesMap.append( "dimensions", dimensionMap );
    valuesMap.append( "boxdims", boxDimensionMap );
    valuesMap.append( "images", imageMap );
    valuesMap.append( "gradients", gradientMap );
    valuesMap.append( "colors", colorMap );
    valuesMap.append( "fonts", fontMap );
    valuesMap.append( "borders", borderMap );
    return valuesMap;
  }

  private void createThemeStoreCss( final Theme theme, boolean isDefault ) {
    ThemeCssValuesMap valuesMap = theme.getValuesMap();
    JsonObject mainObject = new JsonObject();
    for( int i = 0; i < elements.length; i++ ) {
      IThemeCssElement element = elements[ i ];
      String elementName = element.getName();
      JsonObject elementObj = new JsonObject();
      String[] properties = element.getProperties();
      for( int j = 0; j < properties.length; j++ ) {
        String propertyName = properties[ j ];
        JsonArray valuesArray = new JsonArray();
        ConditionalValue[] values
          = valuesMap.getValues( elementName, propertyName );
        for( int k = 0; k < values.length; k++ ) {
          ConditionalValue conditionalValue = values[ k ];
          JsonArray array = new JsonArray();
          array.append( JsonArray.valueOf( conditionalValue.constraints ) );
          array.append( Theme.createCssKey( conditionalValue.value ) );
          valuesArray.append( array );
        }
        elementObj.append( propertyName, valuesArray );
      }
      mainObject.append( elementName, elementObj );
    }
    cssValuesBuffer.append( "ts.setThemeCssValues( " );
    cssValuesBuffer.append( JsonValue.quoteString( theme.getJsId() ) );
    cssValuesBuffer.append( ", " );
    cssValuesBuffer.append( mainObject );
    cssValuesBuffer.append( ", " );
    cssValuesBuffer.append( isDefault );
    cssValuesBuffer.append( " );\n" );
  }
}
