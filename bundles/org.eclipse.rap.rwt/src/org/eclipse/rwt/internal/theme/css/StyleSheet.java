/*******************************************************************************
 * Copyright (c) 2008, 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 *     EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rwt.internal.theme.css;

import java.util.*;

import org.eclipse.rwt.internal.theme.*;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;


/**
 * Instances of this class represent a parsed CSS stylesheet.
 */
public final class StyleSheet {

  private static final SelectorWrapperComparator COMPARATOR
    = new SelectorWrapperComparator();

  private final StyleRule[] styleRules;

  private SelectorWrapper[] selectorWrappers;

  public StyleSheet( final StyleRule[] styleRules ) {
    this.styleRules = styleRules;
    // TODO: [if] Find better solution for merging border and border-radius
    mergeBorderRaduis();
    createSelectorWrappers();
  }

  public StyleRule[] getStyleRules() {
    return styleRules;
  }

  public ConditionalValue[] getValues( final String elementName,
                                       final String propertyName )
  {
    List buffer = new ArrayList();
    for( int i = 0; i < selectorWrappers.length; i++ ) {
      SelectorWrapper selectorWrapper = selectorWrappers[ i ];
      String selectorElement
        = ( ( SelectorExt )selectorWrapper.selector ).getElementName();
      if( selectorElement == null || selectorElement.equals( elementName ) ) {
        QxType value = selectorWrapper.propertyMap.getValue( propertyName );
        if( value != null ) {
          String[] constraints
            = ( ( SelectorExt )selectorWrapper.selector ).getConstraints();
          ConditionalValue condValue
            = new ConditionalValue( constraints, value );
          buffer.add( condValue );
        }
      }
    }
    ConditionalValue[] result = new ConditionalValue[ buffer.size() ];
    buffer.toArray( result );
    return result;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    StyleRule[] styleRules = getStyleRules();
    for( int i = 0; i < styleRules.length; i++ ) {
      StyleRule styleRule = styleRules[ i ];
      SelectorList selectors = styleRule.getSelectors();
      int length = selectors.getLength();
      for( int j = 0; j < length; j++ ) {
        if( j > 0 ) {
          buffer.append( "," );
        }
        if( i > 0 ) {
          buffer.append( "\n" );
        }
        buffer.append( selectors.item( j ) );
      }
      buffer.append( "\n" );
      buffer.append( styleRule.getProperties() );
      buffer.append( "\n" );
    }
    return buffer.toString();
  }

  private void mergeBorderRaduis() {
    for( int i_rule = 0; i_rule < styleRules.length; i_rule++ ) {
      StyleRule styleRule = styleRules[ i_rule ];
      IStylePropertyMap properties = styleRule.getProperties();
      QxBorder border = ( QxBorder )properties.getValue( "border" );
      QxBoxDimensions radius
        = ( QxBoxDimensions )properties.getValue( "border-radius" );
      if( border != null && radius != null ) {
        Rectangle borderRadius = new Rectangle( radius.top,
                                                radius.right,
                                                radius.bottom,
                                                radius.left );
        border.radius = borderRadius;
      }
    }
  }

  private void createSelectorWrappers() {
    ArrayList selectorWrappersList = new ArrayList();
    for( int i_rule = 0; i_rule < styleRules.length; i_rule++ ) {
      StyleRule styleRule = styleRules[ i_rule ];
      SelectorList selectors = styleRule.getSelectors();
      IStylePropertyMap properties = styleRule.getProperties();
      int length = selectors.getLength();
      for( int i_sel = 0; i_sel < length; i_sel++ ) {
        Selector selector = selectors.item( i_sel );
        SelectorWrapper selectorWrapper
          = new SelectorWrapper( selector, properties, i_rule );
        selectorWrappersList.add( selectorWrapper );
      }
    }
    Collections.sort( selectorWrappersList, COMPARATOR );
    Collections.reverse( selectorWrappersList );
    selectorWrappers = new SelectorWrapper[ selectorWrappersList.size() ];
    selectorWrappersList.toArray( selectorWrappers );
  }

  static class SelectorWrapper {

    public final Selector selector;

    public final IStylePropertyMap propertyMap;

    public final int position;

    public SelectorWrapper( final Selector selector,
                            final IStylePropertyMap propertyMap,
                            final int position )
    {
      this.selector = selector;
      this.position = position;
      this.propertyMap = propertyMap;
    }
  }

  private static class SelectorWrapperComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      int result = 0;
      SelectorWrapper selectorWrapper1 = ( SelectorWrapper )object1;
      SelectorWrapper selectorWrapper2 = ( SelectorWrapper )object2;
      int specificity1
        = ( ( Specific )selectorWrapper1.selector ).getSpecificity();
      int specificity2
        = ( ( Specific )selectorWrapper2.selector ).getSpecificity();
      if( specificity1 > specificity2 ) {
        result = 1;
      } else if( specificity1 < specificity2 ) {
        result = -1;
      } else if( selectorWrapper1.position > selectorWrapper2.position ) {
        result = 1;
      } else if( selectorWrapper1.position < selectorWrapper2.position ) {
        result = -1;
      }
      return result;
    }
  }
}
