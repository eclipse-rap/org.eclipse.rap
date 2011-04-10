/*******************************************************************************
 * Copyright (c) 2011 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rwt.internal.theme.QxAnimation.Animation;


public final class ThemePropertyAdapterRegistry {

  public static interface ThemePropertyAdapter {

    /**
     * The slot in the client's theme store to write the value into or <code>null</code> if no value
     * needs to be written.
     */
    String getSlot( QxType value );

    /**
     * The id that references the property in the client's theme store of the value itself if no
     * translation is needed.
     */
    String getKey( QxType value );

    /**
     * The value to write into the client's theme store or <code>null</code> if no value needs to be
     * written.
     */
    JsonValue getValue( QxType value );
  }

  public static class DirectPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return value.toDefaultString();
    }

    public String getSlot( QxType value ) {
      return null;
    }

    public JsonValue getValue( QxType value ) {
      return null;
    }
  }

  public static class DimensionPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "dimensions";
    }

    public JsonValue getValue( QxType value ) {
      return JsonValue.valueOf( ( ( QxDimension )value ).value );
    }
  }

  public static class BoxDimensionsPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "boxdims";
    }

    public JsonValue getValue( QxType value ) {
      QxBoxDimensions boxdim = ( QxBoxDimensions )value;
      JsonArray result = new JsonArray();
      result.append( boxdim.top );
      result.append( boxdim.right );
      result.append( boxdim.bottom );
      result.append( boxdim.left );
      return result;
    }
  }

  public static class FontPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "fonts";
    }

    public JsonValue getValue( QxType value ) {
      QxFont font = ( QxFont )value;
      JsonObject result = new JsonObject();
      result.append( "family", JsonArray.valueOf( font.family ) );
      result.append( "size", font.size );
      result.append( "bold", font.bold );
      result.append( "italic", font.italic );
      return result;
    }
  }

  public static class ImagePropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      String result;
      QxImage image = ( QxImage )value;
      if( image.isGradient() ) {
        result = "gradients";
      } else {
        result = "images";
      }
      return result;
    }

    public JsonValue getValue( QxType value ) {
      QxImage image = ( QxImage )value;
      JsonValue result = null;
      if( image.isGradient() ) {
        JsonObject gradientObject = null;
        gradientObject = new JsonObject();
        JsonArray percents = JsonArray.valueOf( image.gradientPercents );
        gradientObject.append( "percents", percents );
        JsonArray colors = JsonArray.valueOf( image.gradientColors );
        gradientObject.append( "colors", colors );
        gradientObject.append( "vertical", image.vertical );
        result = gradientObject;
      } else if( !image.none ) {
        JsonArray imageArray = new JsonArray();
        imageArray.append( image.width );
        imageArray.append( image.height );
        result = imageArray;
      }
      return result;
    }
  }

  public static class ColorPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "colors";
    }

    public JsonValue getValue( QxType value ) {
      QxColor color = ( QxColor )value;
      JsonValue result;
      if( color.isTransparent() ) {
        result = JsonValue.valueOf( "undefined" );
      } else {
        result = JsonValue.valueOf( QxColor.toHtmlString( color.red, color.green, color.blue ) );
      }
      return result;
    }
  }

  public static class BorderPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "borders";
    }

    public JsonValue getValue( QxType value ) {
      QxBorder border = ( QxBorder )value;
      JsonObject result = new JsonObject();
      result.append( "width", border.width );
      result.append( "style", border.style );
      result.append( "color", border.color );
      return result;
    }
  }

  public static class CursorPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "cursors";
    }

    public JsonValue getValue( QxType value ) {
      QxCursor cursor = ( QxCursor )value;
      JsonValue result;
      if( cursor.isCustomCursor() ) {
        result = JsonValue.NULL;
      } else {
        result = JsonValue.valueOf( cursor.value );
      }
      return result;
    }
  }

  public static class AnimationPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "animations";
    }

    public JsonValue getValue( QxType value ) {
      QxAnimation animation = ( QxAnimation )value;
      JsonObject result = new JsonObject();
      for( int j = 0; j < animation.animations.length; j++ ) {
        Animation currentAnimation = animation.animations[ j ];
        JsonArray currentAnimationArray = new JsonArray();
        currentAnimationArray.append( currentAnimation.duration );
        String timingFunction = QxAnimation.toCamelCaseString( currentAnimation.timingFunction );
        currentAnimationArray.append( timingFunction );
        result.append( currentAnimation.name, currentAnimationArray );
      }
      return result;
    }
  }

  public static class ShadowPropertyAdapter implements ThemePropertyAdapter {

    public String getKey( QxType value ) {
      return Integer.toHexString( value.hashCode() );
    }

    public String getSlot( QxType value ) {
      return "shadows";
    }

    public JsonValue getValue( QxType value ) {
      QxShadow shadow = ( QxShadow )value;
      JsonValue result;
      if( shadow.equals( QxShadow.NONE ) ) {
        result = JsonValue.NULL;
      } else {
        JsonArray array = new JsonArray();
        array.append( shadow.inset );
        array.append( shadow.offsetX );
        array.append( shadow.offsetY );
        array.append( shadow.blur );
        array.append( shadow.spread );
        array.append( shadow.color );
        array.append( shadow.opacity );
        result = array;
      }
      return result;
    }
  }

  // TODO [rst] Create instance in ApplicationContext
  private static ThemePropertyAdapterRegistry instance = new ThemePropertyAdapterRegistry();
  private final Map map;

  private ThemePropertyAdapterRegistry() {
    map = new HashMap();
    map.put( QxAnimation.class, new AnimationPropertyAdapter() );
    map.put( QxBoolean.class, new DirectPropertyAdapter() );
    map.put( QxBorder.class, new BorderPropertyAdapter() );
    map.put( QxBoxDimensions.class, new BoxDimensionsPropertyAdapter() );
    map.put( QxColor.class, new ColorPropertyAdapter() );
    map.put( QxCursor.class, new CursorPropertyAdapter() );
    map.put( QxDimension.class, new DimensionPropertyAdapter() );
    map.put( QxFloat.class, new DirectPropertyAdapter() );
    map.put( QxFont.class, new FontPropertyAdapter() );
    map.put( QxIdentifier.class, new DirectPropertyAdapter() );
    map.put( QxImage.class, new ImagePropertyAdapter() );
    map.put( QxShadow.class, new ShadowPropertyAdapter() );
  }

  public static ThemePropertyAdapterRegistry getInstance() {
    return instance;
  }

  public ThemePropertyAdapter getPropertyAdapter( Class key ) {
    return ( ThemePropertyAdapter )map.get( key );
  }
}
