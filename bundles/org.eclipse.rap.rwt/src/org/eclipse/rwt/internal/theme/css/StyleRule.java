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

import org.w3c.css.sac.SelectorList;

/**
 * Instances of this class represent a single rule in a CSS style sheet
 * including selector list and property map.
 */
public class StyleRule {

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
}
