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

import org.eclipse.rwt.internal.theme.QxType;
import org.eclipse.rwt.internal.theme.ResourceLoader;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;


/**
 * Instances of this class represent a parsed CSS stylesheet.
 */
public class StyleSheet {

  private static final SelectorWrapperComparator COMPARATOR
    = new SelectorWrapperComparator();

  private final StyleRule[] styleRules;

  private SelectorWrapper[] selectorWrappers;

  public StyleSheet( final StyleRule[] styleRules ) {
    this.styleRules = styleRules;
    createSelectorWrappers();
  }

  public StyleRule[] getStyleRules() {
    return styleRules;
  }

  /**
   * Returns all style rules that apply to elements of the given name, including
   * wildcard and conditional rules.
   */
  public SelectorWrapper[] getMatchingStyleRules( final String elementName ) {
    List buffer = new ArrayList();
    for( int i = 0; i < selectorWrappers.length; i++ ) {
      SelectorWrapper selectorWrapper = selectorWrappers[ i ];
      String selectorElement = selectorWrapper.selectorExt.getElementName();
      if( selectorElement == null || selectorElement.equals( elementName ) ) {
        buffer.add( selectorWrapper );
      }
    }
    SelectorWrapper[] result = new SelectorWrapper[ buffer.size() ];
    buffer.toArray( result );
    return result;
  }

  public SelectorWrapper[] getMatchingStyleRules( final StylableElement element )
  {
    List buffer = new ArrayList();
    for( int i = 0; i < selectorWrappers.length; i++ ) {
      SelectorWrapper ruleWrapper = selectorWrappers[ i ];
      if( ruleWrapper.selectorExt.matches( element ) ) {
        buffer.add( ruleWrapper );
      }
    }
    SelectorWrapper[] result = new SelectorWrapper[ buffer.size() ];
    buffer.toArray( result );
    return result;
  }

  public QxType getValue( final String cssProperty,
                          final StylableElement element,
                          final ResourceLoader loader )
  {
    QxType result = null;
    SelectorWrapper[] selectorWrappers = getMatchingSelectors( element );
    for( int i = 0; i < selectorWrappers.length && result == null; i++ ) {
      IStylePropertyMap properties = selectorWrappers[ i ].propertyMap;
      QxType value = properties.getValue( cssProperty, loader );
      if( value != null ) {
        result = value;
      }
    }
    return result;
  }

  public String[] getVariants( final String elementName ) {
    List classesList = new ArrayList();
    for( int i = 0; i < selectorWrappers.length; i++ ) {
      SelectorExt selectorExt = selectorWrappers[ i ].selectorExt;
      if( selectorExt.getElementName() == null
          || selectorExt.getElementName().equals( elementName ) )
      {
        String[] classes = selectorExt.getClasses();
        if( classes != null ) {
          for( int j = 0; j < classes.length; j++ ) {
            String className = classes[ j ];
            if( !classesList.contains( className ) ) {
              classesList.add( className );
            }
          }
        }
      }
    }
    String[] result = new String[ classesList.size() ];
    classesList.toArray( result  );
    return result;
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

  private SelectorWrapper[] getMatchingSelectors( final StylableElement element )
  {
    List resultList = new ArrayList();
    for( int i = 0; i < selectorWrappers.length; i++ ) {
      SelectorWrapper selectorWrapper = selectorWrappers[ i ];
      if( selectorWrapper.selectorExt.matches( element ) ) {
        resultList.add( selectorWrapper );
      }
    }
    SelectorWrapper[] result = new SelectorWrapper[ resultList.size() ];
    resultList.toArray( result );
    return result;
  }

  static class SelectorWrapperComparator implements Comparator {

    public int compare( final Object object1, final Object object2 ) {
      int result = 0;
      SelectorWrapper selectorWrapper1 = ( SelectorWrapper )object1;
      SelectorWrapper selectorWrapper2 = ( SelectorWrapper )object2;
      int specificity1 = selectorWrapper1.selectorExt.getSpecificity();
      int specificity2 = selectorWrapper2.selectorExt.getSpecificity();
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

  public static class SelectorWrapper {

    public final Selector selector;

    public final SelectorExt selectorExt;

    public final IStylePropertyMap propertyMap;
    
    public final int position;

    public SelectorWrapper( final Selector selector,
                            final IStylePropertyMap propertyMap,
                            final int position )
    {
      this.selector = selector;
      this.selectorExt = ( SelectorExt )selector;
      this.position = position;
      this.propertyMap = propertyMap;
    }
  }
}
