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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.rwt.internal.theme.css.*;
import org.w3c.css.sac.*;


/**
 * Utility class that provides static methods to help supporting the old
 * property-based theming.
 */
public class PropertySupport {

  private static final class SimpleSelectorList implements SelectorList {

    private final List selectors = new ArrayList();

    public void add( final Selector selector ) {
      selectors.add( selector );
    }

    public int getLength() {
      return selectors.size();
    }

    public Selector item( final int index ) {
      return ( Selector )selectors.get( index );
    }
  }

  private static final class SimplePropertyMap implements IStylePropertyMap {

    private final Map properties = new HashMap();

    public void add( final String key, final QxType value ) {
      properties.put( key, value );
    }

    public QxBorder getBorder( final String propertyName ) {
      return ( QxBorder )properties.get( propertyName );
    }

    public QxBoxDimensions getBoxDimensions( final String propertyName ) {
      return ( QxBoxDimensions )properties.get( propertyName );
    }

    public QxColor getColor( final String propertyName ) {
      return ( QxColor )properties.get( propertyName );
    }

    public QxDimension getDimension( final String propertyName ) {
      return ( QxDimension )properties.get( propertyName );
    }

    public QxFont getFont( final String propertyName ) {
      return ( QxFont )properties.get( propertyName );
    }

    public QxImage getImage( final String propertyName ) {
      return ( QxImage )properties.get( propertyName );
    }

    public String[] getProperties() {
      Set keySet = properties.keySet();
      String[] result = new String[ keySet.size() ];
      keySet.toArray( result );
      return result;
    }

    public QxType getValue( final String propertyName ) {
      return ( QxType )properties.get( propertyName );
    }

    public QxType getValue( final String propertyName, final Class expectedType )
    {
      return ( QxType )properties.get( propertyName );
    }
  }

  private static final Pattern CSS_CONDITION_ATTR_PATTERN
    = Pattern.compile( "\\[([A-Z]+)\\]|:([a-z]+)|.+" );

  static StylableElement createDummyElement( final ThemeProperty property,
                                             final String variant )
  {
    String elementName = property.cssElements[ 0 ];
    String conditionStr = null;
    if( property.cssSelectors.length > 0 ) {
      conditionStr = property.cssSelectors[ 0 ];
    }
    return createDummyElement( elementName, conditionStr, variant );
  }

  static StylableElement createDummyElement( final String elementName,
                                             final String conditionStr,
                                             final String variant )
  {
    StylableElement result = new StylableElement( elementName );
    if( conditionStr != null ) {
      Matcher matcher = CSS_CONDITION_ATTR_PATTERN.matcher( conditionStr );
      while( matcher.find() ) {
        String style = matcher.group( 1 );
        String state = matcher.group( 2 );
        if( style != null ) {
          result.setAttribute( style );
        } else if( state != null ) {
          result.setPseudoClass( state );
        } else {
          System.err.println( "Garbage found in css-selectors attribute: "
                              + matcher.group() );
        }
      }
    }
    if( variant != null ) {
      result.setClass( variant );
    }
    return result;
  }

  static StyleSheet createStyleSheetFromProperties( final ThemeProperty[] properties,
                                                    final Theme theme ) {
    StyleSheetBuilder styleSheetBuilder = new StyleSheetBuilder();
    String[] variants = getVariants( theme );
    for( int i = 0; i < properties.length; i++ ) {
      ThemeProperty property = properties[ i ];
      if( property.cssElements.length > 0 && property.cssProperty != null ) {
        QxType value = theme.getValue( property.name );
        StyleRule styleRule = createStyleRule( property, null, value );
        styleSheetBuilder.addStyleRule( styleRule );
        for( int j = 0; j < variants.length; j++ ) {
          String variant = variants[ j ];
          QxType vValue = theme.getValue( property.name, variant );
          if( !vValue.equals( value ) ) {
            StyleRule vStyleRule = createStyleRule( property, variant, vValue );
            styleSheetBuilder.addStyleRule( vStyleRule );
          }
        }
      } else {
        System.err.println( "Property without CSS support: " + property.name );
      }
    }
    return styleSheetBuilder.getStyleSheet();
  }

  static String[] getVariants( final Theme theme ) {
    Set variants = new HashSet();
    String[] keys = theme.getKeysWithVariants();
    for( int i = 0; i < keys.length; i++ ) {
      String key = keys[ i ];
      int index = key.indexOf( '/' );
      if( index != -1 ) {
        String variant = key.substring( 0, index );
        variants.add( variant );
      }
    }
    String[] result = new String[ variants.size() ];
    variants.toArray( result );
    return result;
  }

  private static StyleRule createStyleRule( final ThemeProperty property,
                                            final String variant,
                                            final QxType value )
  {
    SimpleSelectorList selectors = new SimpleSelectorList();
    SimplePropertyMap propertyMap = new SimplePropertyMap();
    propertyMap.add( property.cssProperty, value );
    for( int j = 0; j < property.cssElements.length; j++ ) {
      String element = property.cssElements[ j ];
      if( property.cssSelectors != null && property.cssSelectors.length > 0 )
      {
        for( int k = 0; k < property.cssSelectors.length; k++ ) {
          String selector = property.cssSelectors[ k ];
          selectors.add( createSelector( element, variant, selector ) );
        }
      } else {
        selectors.add( createSelector( element, variant, null ) );
      }
    }
    StyleRule styleRule = new StyleRule( selectors, propertyMap );
    return styleRule;
  }

  private static Selector createSelector( final String elementName,
                                          final String variant,
                                          final String conditionStr )
  {
    Selector result;
    ElementSelectorImpl elementSelector = new ElementSelectorImpl( elementName );
    Condition condition = null;
    if( conditionStr != null ) {
      Matcher matcher = CSS_CONDITION_ATTR_PATTERN.matcher( conditionStr );
      while( matcher.find() ) {
        String style = matcher.group( 1 );
        String state = matcher.group( 2 );
        Condition nextCondition;
        if( style != null ) {
          nextCondition = new AttributeConditionImpl( style, null, false );
        } else if( state != null ) {
          nextCondition = new PseudoClassConditionImpl( state );
        } else {
          String mesg = "Garbage found in css-selectors attribute: "
            + matcher.group();
          throw new IllegalArgumentException( mesg );
        }
        if( condition == null ) {
          condition = nextCondition;
        } else {
          condition = new AndConditionImpl( condition, nextCondition );
        }
      }
      if( variant != null ) {
        Condition nextCondition = new ClassConditionImpl( variant );
        if( condition == null ) {
          condition = nextCondition ;
        } else {
          condition = new AndConditionImpl( condition, nextCondition );
        }
      }
    }
    if( condition != null ) {
      result = new ConditionalSelectorImpl( elementSelector, condition );
    } else {
      result = elementSelector;
    }
    return result;
  }
}
