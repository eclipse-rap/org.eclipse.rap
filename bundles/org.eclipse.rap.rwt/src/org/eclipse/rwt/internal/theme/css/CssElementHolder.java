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

import org.eclipse.rwt.internal.theme.IThemeCssElement;


/**
 * Holds all registered {@link IThemeCssElement}s.
 */
public class CssElementHolder {

  private final Map elements;

  public CssElementHolder() {
    elements = new HashMap();
  }

  public void addElement( final IThemeCssElement element ) {
    if( elements.containsKey( element.getName() ) ) {
      String message = "An element with this name is already defined: "
                       + element.getName();
      throw new IllegalArgumentException( message );
    }
    elements.put( element.getName(), element );
  }

  public IThemeCssElement[] getAllElements() {
    Collection values = elements.values();
    int size = values.size();
    IThemeCssElement[] result = new IThemeCssElement[ size ];
    values.toArray( result );
    return result;
  }

  public void clear() {
    elements.clear();
  }
}
