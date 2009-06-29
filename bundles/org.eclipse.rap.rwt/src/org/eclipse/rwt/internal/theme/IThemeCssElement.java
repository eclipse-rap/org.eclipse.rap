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
 * A DOM element that represents a themeable widget or a sub-widget and can be
 * referred to in CSS style sheets.
 */
public interface IThemeCssElement {

  public abstract String getName();

  public abstract String[] getProperties();

  public abstract String[] getStyles();

  public abstract String[] getStates();
}
