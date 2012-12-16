/*******************************************************************************
 * Copyright (c) 2002, 2012 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.rap.rwt.lifecycle;

import org.eclipse.swt.widgets.Widget;


/**
 * @deprecated Use {@link WidgetAdapter} instead.
 * @since 2.0
 */
@Deprecated
public interface IWidgetAdapter {

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  String getId();

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  Widget getParent();

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  boolean isInitialized();

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  void preserve( String propertyName, Object value );

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  Object getPreserved( String propertyName );

  /**
   * @deprecated Use {@link WidgetAdapter} instead of {@link IWidgetAdapter}.
   */
  @Deprecated
  void markDisposed( Widget widget );

}
