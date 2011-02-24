/*******************************************************************************
 * Copyright (c) 2010 EclipseSource and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rwt.internal.theme;

import java.util.Arrays;


public class QxAnimation implements QxType {

  public static final class Animation {

    private final String[] PREDEFINED_NAMES = new String[] {
      "hoverIn",
      "hoverOut",
      "fadeIn",
      "fadeOut",
      "slideIn",
      "slideOut"
    };

    private final String[] PREDEFINED_TIMING_FUNCTIONS = new String[] {
      "ease",
      "linear",
      "ease-in",
      "ease-out",
      "ease-in-out"
    };

    public final String name;
    public final int duration;
    public final String timingFunction;

    private Animation( final String name,
                       final int duration,
                       final String timingFunction )
    {
      checkName( name );
      checkTimingFunction( timingFunction );
      this.name = name;
      this.duration = duration;
      this.timingFunction = timingFunction;
    }

    private void checkName( final String name ) {
      boolean result = false;
      if( name == null ) {
        throw new NullPointerException( "null argument" );
      }
      for( int i = 0; i < PREDEFINED_NAMES.length && !result; i++ ) {
        if( name.equals( PREDEFINED_NAMES[ i ] ) ) {
          result = true;
        }
      }
      if( !result ) {
        String msg = "Invalid value for animation name: " + name;
        throw new IllegalArgumentException( msg );
      }
    }

    private void checkTimingFunction( final String timingFunction ) {
      boolean result = false;
      if( timingFunction == null ) {
        throw new NullPointerException( "null argument" );
      }
      for( int i = 0; i < PREDEFINED_TIMING_FUNCTIONS.length && !result; i++ ) {
        if( timingFunction.equals( PREDEFINED_TIMING_FUNCTIONS[ i ] ) ) {
          result = true;
        }
      }
      if( !result ) {
        String msg = "Invalid value for animation timing function: "
                     + timingFunction;
        throw new IllegalArgumentException( msg );
      }
    }

    public boolean equals( final Object object ) {
      boolean result = false;
      if( object == this ) {
        result = true;
      } else if( object instanceof Animation ) {
        Animation other = ( Animation )object;
        result = name.equals( other.name )
                 && duration == other.duration
                 && timingFunction.equals( other.timingFunction );
      }
      return result;
    }

  }

  public Animation[] animations;

  public QxAnimation() {
    animations = new Animation[ 0 ];
  }

  public void addAnimation( final String name,
                            final int duration,
                            final String timingFunction )
  {
    Animation animation = new Animation( name, duration, timingFunction );
    Animation[] newAnimations = new Animation[ animations.length + 1 ];
    System.arraycopy( animations, 0, newAnimations, 0, animations.length );
    newAnimations[ animations.length ] = animation;
    animations = newAnimations;
  }

  public String toDefaultString() {
    StringBuffer result = new StringBuffer();
    for( int i = 0; i < animations.length; i++ ) {
      if( result.length() != 0 ) {
        result.append( ", " );
      }
      result.append( animations[ i ].name );
      result.append( " " );
      result.append( animations[ i ].duration );
      result.append( "ms " );
      result.append( animations[ i ].timingFunction );
    }
    return result.toString();
  }

  public String toString() {
    return "QxAnimation{ " + toDefaultString() + " }";
  }

  public static String toCamelCaseString( final String string ) {
    StringBuffer result = new StringBuffer();
    boolean toUpperCase = false;
    for( int i = 0; i < string.length(); i++ ) {
      char ch = string.charAt( i );
      if( ch == '-' ) {
        toUpperCase = true;
      } else if( toUpperCase ) {
        result.append( Character.toUpperCase( ch ) );
        toUpperCase = false;
      } else {
        result.append( ch );
      }
    }
    return result.toString();
  }

  public int hashCode() {
    int result = 7;
    for( int i = 0; i < animations.length; i++ ) {
      result += 13 * result + animations[ i ].name.hashCode();
      result += 13 * result + animations[ i ].duration;
      result += 13 * result + animations[ i ].timingFunction.hashCode();
    }
    return result;
  }

  public boolean equals( final Object object ) {
    boolean result = false;
    if( object == this ) {
      result = true;
    } else if( object instanceof QxAnimation ) {
      QxAnimation other = ( QxAnimation )object;
      result = Arrays.equals( animations, other.animations );
    }
    return result;
  }
}
