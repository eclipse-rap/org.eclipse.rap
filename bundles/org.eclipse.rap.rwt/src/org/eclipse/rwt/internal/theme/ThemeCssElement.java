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
import java.util.Collection;


public class ThemeCssElement implements IThemeCssElement {

  private final String name;

  private String description;

  private Collection properties;

  private Collection styles;

  private Collection states;

  public ThemeCssElement( final String name ) {
    this.name = name;
    this.properties = new ArrayList();
    this.styles = new ArrayList();
    this.states = new ArrayList();
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public IThemeCssProperty[] getProperties() {
    IThemeCssProperty[] result = new IThemeCssProperty[ properties.size() ];
    properties.toArray( result );
    return result;
  }

  public IThemeCssAttribute[] getStyles() {
    IThemeCssAttribute[] result = new IThemeCssAttribute[ styles.size() ];
    styles.toArray( result );
    return result;
  }
  
  public IThemeCssAttribute[] getStates() {
    IThemeCssAttribute[] result = new IThemeCssAttribute[ states.size() ];
    states.toArray( result );
    return result;
  }

  public void setDescription( final String description ) {
    this.description = description;
  }

  public void addProperty( final IThemeCssProperty property ) {
    properties.add( property );
  }

  public void addStyle( final IThemeCssAttribute style ) {
    styles.add( style );
  }

  public void addState( final IThemeCssAttribute state ) {
    states.add( state );
  }
}
