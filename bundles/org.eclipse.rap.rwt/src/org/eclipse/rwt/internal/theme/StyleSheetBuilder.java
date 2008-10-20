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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.rwt.internal.theme.css.StyleRule;
import org.eclipse.rwt.internal.theme.css.StyleSheet;


public class StyleSheetBuilder {

  private final List rulesList;

  public StyleSheetBuilder() {
    rulesList = new ArrayList();
  }

  public void addStyleSheet( final StyleSheet styleSheet ) {
    StyleRule[] styleRules = styleSheet.getStyleRules();
    for( int i = 0; i < styleRules.length; i++ ) {
      StyleRule styleRule = styleRules[ i ];
      addStyleRule( styleRule );
    }
  }

  public void addStyleRule( final StyleRule styleRule ) {
    rulesList.add( styleRule );
  }

  public StyleSheet getStyleSheet() {
    StyleRule[] styleRules = new StyleRule[ rulesList.size() ];
    rulesList.toArray( styleRules );
    return new StyleSheet( styleRules );
  }
}
