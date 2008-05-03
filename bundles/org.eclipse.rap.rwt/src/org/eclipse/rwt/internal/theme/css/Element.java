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


public interface Element {

  abstract boolean hasName( String name );

  abstract boolean hasClass( String name );

  abstract boolean hasPseudoClass( String name );

  abstract boolean hasAttribute( String name );

  abstract String getAttribute( String name );

  abstract Element getParent();
}
