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

package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.*;
import org.w3c.css.sac.*;


public class StyleSheet {


  private static final MatchedStyleRuleComparator COMPARATOR
    = new MatchedStyleRuleComparator();

  private final StyleRule[] styleRules;

  private String[] variants;

  public StyleSheet( final StyleRule[] styleRules ) {
    this.styleRules = styleRules;
    findClasses();
  }

  public StyleRule[] getStyleRules() {
    return styleRules;
  }

  public StyleRule[] getMatchingStyleRules( final Element element ) {
    List buffer = new ArrayList();
    for( int i = 0; i < styleRules.length; i++ ) {
      StyleRule rule = styleRules[ i ];
      Selector selector = rule.getMatchingSelector( element );
      if( selector != null ) {
        int specificity = ( ( Specific )selector ).getSpecificity();
        buffer.add( new MatchedStyleRule( rule, specificity, i ) );
      }
    }
    Collections.sort( buffer, COMPARATOR );
    StyleRule[] result = new StyleRule[ buffer.size() ];
    for( int i = 0; i < result.length; i++ ) {
      result[ i ] = ( ( MatchedStyleRule )buffer.get( i ) ).rule;
    }
    return result;
  }

  public QxType getValue( final String cssProperty,
                          final StylableElement element,
                          final ResourceLoader loader )
  {
    QxType result = null;
    StyleRule[] rules = getMatchingStyleRules( element );
    for( int i = 0; i < rules.length; i++ ) {
      StyleRule rule = rules[ i ];
      StylePropertyMap properties = rule.getProperties();
      LexicalUnit property = properties.getProperty( cssProperty );
      if( property != null ) {
        if( "color".equals( cssProperty ) ) {
          result = readColor( properties );
        } else if( "background-color".equals( cssProperty ) ) {
          result = readBackgroundColor( properties );
        } else if( "background-image".equals( cssProperty ) ) {
          result = readBackgroundImage( properties, loader );
        } else if( "border".equals( cssProperty ) ) {
          result = readBorder( properties );
        } else if( "padding".equals( cssProperty ) ) {
          result = readPadding( properties );
        } else if( "margin".equals( cssProperty ) ) {
          result = readMargin( properties );
        } else if( "spacing".equals( cssProperty ) ) {
          result = readSpacing( properties );
        } else if( "height".equals( cssProperty ) ) {
          result = readHeight( properties );
        } else if( "width".equals( cssProperty ) ) {
          result = readWidth( properties );
        } else if( "font".equals( cssProperty ) ) {
          result = readFont( properties );
        } else if( cssProperty.startsWith( "rwt" )
                   && cssProperty.endsWith( "color" ) )
        {
          result = readColor( properties, cssProperty );
        } else if( "background-gradient-color".equals( cssProperty ) ) {
          result = readBackgroundColor( properties, cssProperty );
        } else {
//          TODO [rst] Logging instead of sysout
          System.err.println( "unsupported css property: " + cssProperty );
        }
      }
    }
    return result;
  }

  public String[] getVariants( final String elementName ) {
    return variants;
  }

  private void findClasses() {
    List list = new ArrayList();
    for( int i = 0; i < styleRules.length; i++ ) {
      StyleRule rule = styleRules[ i ];
      SelectorList selectors = rule.getSelectors();
      int length = selectors.getLength();
      for( int j = 0; j < length; j++ ) {
        SelectorExt selector = ( SelectorExt )selectors.item( j );
        String[] classes = selector.getClasses();
        if( classes != null ) {
          for( int k = 0; k < classes.length; k++ ) {
            String variant = classes[ k ];
            if( !list.contains( variant ) ) {
              list.add( variant );
//              System.out.println( "found variant: " + variant );
            }
          }
        }
      }
    }
    variants = new String[ list.size() ];
    list.toArray( variants );
  }

  private static QxColor readColor( final StylePropertyMap properties ) {
    return readColor( properties, "color" );
  }

  private static QxColor readColor( final StylePropertyMap properties,
                             final String property )
  {
    LexicalUnit unit = properties.getProperty( property );
    QxColor result = PropertyResolver.readColor( unit );
    return result != QxColor.TRANSPARENT ? result : null;
  }

  private static QxColor readBackgroundColor( final StylePropertyMap properties )
  {
    return readBackgroundColor( properties, "background-color" );
  }
  
  private static QxColor readBackgroundColor( final StylePropertyMap properties,
                                              final String property )
  {
    LexicalUnit unit = properties.getProperty( property );
    return PropertyResolver.readColor( unit );
  }

  private QxFont readFont( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "font" );
    return PropertyResolver.readFont( property );
  }

  private QxImage readBackgroundImage( final StylePropertyMap properties,
                                       final ResourceLoader loader ) {
    LexicalUnit property = properties.getProperty( "background-image" );
    return PropertyResolver.readBackgroundImage( property, loader );
  }

  private QxBorder readBorder( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "border" );
    return PropertyResolver.readBorder( property );
  }

  private QxBoxDimensions readPadding( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "padding" );
    return PropertyResolver.readBoxDimensions( property );
  }

  private QxBoxDimensions readMargin( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "margin" );
    return PropertyResolver.readBoxDimensions( property );
  }

  private QxDimension readSpacing( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "spacing" );
    return PropertyResolver.readDimension( property );
  }

  private QxDimension readHeight( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "height" );
    return PropertyResolver.readDimension( property );
  }
  
  private QxDimension readWidth( final StylePropertyMap properties ) {
    LexicalUnit property = properties.getProperty( "width" );
    return PropertyResolver.readDimension( property );
  }

  static class MatchedStyleRuleComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      MatchedStyleRule rule1 = ( MatchedStyleRule )object1;
      MatchedStyleRule rule2 = ( MatchedStyleRule )object2;
      int result = 0;
      if( rule1.specificity > rule2.specificity ) {
        result = 1;
      } else if( rule1.specificity < rule2.specificity ) {
        result = -1;
      } else if( rule1.position > rule2.position ) {
        result = 1;
      } else if( rule1.position < rule2.position ) {
        result = -1;
      }
      return result;
    }
  }

  static class MatchedStyleRule {

    public final StyleRule rule;

    public final int specificity;

    public final int position;

    public MatchedStyleRule( final StyleRule rule,
                             final int specificity,
                             final int position )
    {
      this.rule = rule;
      this.specificity = specificity;
      this.position = position;
    }
  }
}
