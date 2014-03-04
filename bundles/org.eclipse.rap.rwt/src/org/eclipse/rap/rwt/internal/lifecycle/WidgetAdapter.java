/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.lifecycle;

import org.eclipse.rap.rwt.lifecycle.IWidgetAdapter;
import org.eclipse.swt.widgets.Widget;


@SuppressWarnings( "deprecation" )
public interface WidgetAdapter extends IWidgetAdapter {

  String getId();

  Widget getParent();

  boolean isInitialized();

  void preserve( String propertyName, Object value );

  Object getPreserved( String propertyName );

  void markDisposed( Widget widget );

}
