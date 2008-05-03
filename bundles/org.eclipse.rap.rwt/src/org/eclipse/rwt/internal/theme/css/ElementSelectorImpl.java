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

import org.w3c.css.sac.ElementSelector;


public class ElementSelectorImpl implements ElementSelector, SelectorExt {

  private final String tagName;

  public ElementSelectorImpl( final String tagName ) {
    this.tagName = tagName;
  }

  public String getLocalName() {
    return tagName;
  }

  public String getNamespaceURI() {
    return null;
  }

  public short getSelectorType() {
    return SAC_ELEMENT_NODE_SELECTOR;
  }

  public boolean matches( final Element element ) {
    boolean result = false;
    if( element != null ) {
      result = tagName == null || element.hasName( tagName );
    }
    return result;
  }

  public int getSpecificity() {
    return tagName != null ? ELEMENT_SPEC : 0;
  }

  public String getElementName() {
    return tagName;
  }

  public String[] getClasses() {
    return null;
  }

  public String toString() {
    return tagName != null ? tagName : "*";
  }
}
