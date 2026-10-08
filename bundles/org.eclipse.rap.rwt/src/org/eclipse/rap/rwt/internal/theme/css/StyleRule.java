/*******************************************************************************
 * Copyright (c) 2008, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme.css;

import org.w3c.css.sac.SelectorList;

/**
 * Instances of this class represent a single rule in a CSS style sheet
 * including selector list and property map.
 */
public class StyleRule {

  private final SelectorList selectors;

  private final StylePropertyMap properties;

  public StyleRule( SelectorList selectors, StylePropertyMap properties ) {
    this.selectors = selectors;
    this.properties = properties;
  }

  public SelectorList getSelectors() {
    return selectors;
  }

  public StylePropertyMap getProperties() {
    return properties;
  }

}
