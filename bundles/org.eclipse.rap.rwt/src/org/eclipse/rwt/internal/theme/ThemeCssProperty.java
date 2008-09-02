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


/**
 * A property in a theme definition file (*.theme.xml).
 */
public class ThemeCssProperty implements IThemeCssProperty {

  private final String name;
  private String description;

  public ThemeCssProperty( final String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( final String description ) {
    this.description = description;
  }
}
