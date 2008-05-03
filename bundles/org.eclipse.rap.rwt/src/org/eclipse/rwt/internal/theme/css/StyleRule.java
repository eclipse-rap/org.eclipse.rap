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

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;


public class StyleRule implements ElementMatcher {

  private final SelectorList selectors;

  private final StylePropertyMap properties;

  public StyleRule( final SelectorList selectors,
                    final StylePropertyMap properties )
  {
    this.selectors = selectors;
    this.properties = properties;
  }

  public SelectorList getSelectors() {
    return selectors;
  }

  public StylePropertyMap getProperties() {
    return properties;
  }

  public Selector getMatchingSelector( final Element element ) {
    Selector result = null;
    int maxSpecificity = -1;
    int length = selectors.getLength();
    for( int i = 0; i < length; i++ ) {
      Selector selector = selectors.item( i );
      ElementMatcher matcher = ( ElementMatcher )selector;
      if( matcher.matches( element ) ) {
        int specificity = ( ( Specific )selector ).getSpecificity();
        if( specificity > maxSpecificity  ) {
          result = selector;
          maxSpecificity = specificity;
        }
      }
    }
    return result;
  }

  public boolean matches( final Element element ) {
    return getMatchingSelector( element ) != null;
  }

  public String getSelectorText() {
    StringBuffer buffer = new StringBuffer();
    int length = selectors.getLength();
    for( int i = 0; i < length; i++ ) {
      Selector selector = selectors.item( i );
      if( i > 0 ) {
        buffer.append( ", " );
      }
      buffer.append( selector.toString() );
    }
    return buffer.toString();
  }
}
