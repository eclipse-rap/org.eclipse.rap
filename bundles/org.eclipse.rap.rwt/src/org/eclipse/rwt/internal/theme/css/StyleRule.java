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

/**
 * Instances of this class represent a single rule in a CSS style sheet
 * including selector list and property map.
 */
public class StyleRule implements ElementMatcher {

  private final SelectorList selectors;

  private final IStylePropertyMap properties;

  public StyleRule( final SelectorList selectors,
                    final IStylePropertyMap properties )
  {
    this.selectors = selectors;
    this.properties = properties;
  }

  public SelectorList getSelectors() {
    return selectors;
  }

  public IStylePropertyMap getProperties() {
    return properties;
  }

  /**
   * Returns the selector with the highest specificity that matches the given
   * element. If none of the selectors match, <code>null</code> is returned.
   */
  public Selector getMatchingSelector( final Element element ) {
    Selector result = null;
    int maxSpecificity = -1;
    int length = selectors.getLength();
    for( int i = 0; i < length; i++ ) {
      Selector selector = selectors.item( i );
      ElementMatcher matcher = ( ElementMatcher )selector;
      if( matcher.matches( element ) ) {
        int specificity = ( ( Specific )selector ).getSpecificity();
        if( specificity > maxSpecificity ) {
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
}
