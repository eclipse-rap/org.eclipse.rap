/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.template;

public interface Cell {

  Cell setName( String name );

  Cell setBindingIndex( int index );

  Cell setSelectable( boolean selectable );

  Cell setLeft( int offset );

  Cell setRight( int offset );

  Cell setTop( int offset );

  Cell setBottom( int offset );

  Cell setWidth( int width );

  Cell setHeight( int height );
}
