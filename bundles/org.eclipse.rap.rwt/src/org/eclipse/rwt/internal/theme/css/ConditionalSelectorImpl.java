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

import org.w3c.css.sac.*;


public class ConditionalSelectorImpl
  implements ConditionalSelector, SelectorExt
{

  private final SimpleSelector selector;
  private final Condition condition;

  public ConditionalSelectorImpl( final SimpleSelector selector,
                                  final Condition condition )
  {
    this.selector = selector;
    this.condition = condition;
  }

  public Condition getCondition() {
    return condition;
  }

  public SimpleSelector getSimpleSelector() {
    return selector;
  }

  public short getSelectorType() {
    return SAC_CONDITIONAL_SELECTOR;
  }

  public boolean matches( final Element element ) {
    ElementMatcher conditionMatcher = ( ElementMatcher )condition;
    ElementMatcher selectorMatcher = ( ElementMatcher )selector;
    return selectorMatcher.matches( element )
           && conditionMatcher.matches( element );
  }

  public int getSpecificity() {
    Specific specificSelector = (Specific)selector;
    Specific specificCondition = (Specific)condition;
    return specificSelector.getSpecificity()
           + specificCondition.getSpecificity();
  }

  public String getElementName() {
    return ( ( SelectorExt )selector ).getElementName();
  }

  public String[] getClasses() {
    return ( ( ConditionExt )condition ).getClasses();
  }

  public String toString() {
    return selector.toString() + condition.toString();
  }
}
