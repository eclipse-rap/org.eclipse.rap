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

import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;


public class ChildSelectorImpl implements DescendantSelector, SelectorExt {

  private final Selector parent;
  private final SimpleSelector child;

  public ChildSelectorImpl( Selector parent, SimpleSelector child ) {
    this.parent = parent;
    this.child = child;
  }

  @Override
  public Selector getAncestorSelector() {
    return parent;
  }

  @Override
  public SimpleSelector getSimpleSelector() {
    return child;
  }

  @Override
  public short getSelectorType() {
    return SAC_CHILD_SELECTOR;
  }

  @Override
  public String getElementName() {
    return ( ( SelectorExt )child ).getElementName();
  }

  @Override
  public int getSpecificity() {
    return ( ( Specific )parent ).getSpecificity()
           + ( ( Specific )child ).getSpecificity();
  }

  @Override
  public String[] getConstraints() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return parent.toString() + " > " + child.toString();
  }

}
