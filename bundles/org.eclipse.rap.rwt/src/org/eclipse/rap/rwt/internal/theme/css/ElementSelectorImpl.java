/*******************************************************************************
 * Copyright (c) 2008, 2015 Innoopract Informationssysteme GmbH and others.
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

import org.w3c.css.sac.ElementSelector;


public class ElementSelectorImpl implements ElementSelector, SelectorExt {

  private static final String[] EMPTY_STRING_ARRAY = new String[ 0 ];
  private final String tagName;

  public ElementSelectorImpl( String tagName ) {
    this.tagName = tagName;
  }

  @Override
  public String getLocalName() {
    return tagName;
  }

  @Override
  public String getNamespaceURI() {
    return null;
  }

  @Override
  public short getSelectorType() {
    return SAC_ELEMENT_NODE_SELECTOR;
  }

  @Override
  public int getSpecificity() {
    return tagName != null ? ELEMENT_SPEC : 0;
  }

  @Override
  public String getElementName() {
    return tagName;
  }

  @Override
  public String[] getConstraints() {
    return EMPTY_STRING_ARRAY;
  }

  @Override
  public String toString() {
    return tagName != null ? tagName : "*";
  }

}
