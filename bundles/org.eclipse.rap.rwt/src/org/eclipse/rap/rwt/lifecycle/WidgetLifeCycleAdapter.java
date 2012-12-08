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

import java.io.IOException;

import org.eclipse.swt.widgets.Widget;


/**
 * A stateless callback handler used by RWT to synchronize the client-side and server-side state of
 * a widget. Each widget type should provide its own implementation of this interface. Clients
 * should not directly implement this interface, but extend {@link AbstractWidgetLCA} instead.
 *
 * @see AbstractWidgetLCA
 * @see WidgetLCAUtil
 * @since 2.0
 */
public interface WidgetLifeCycleAdapter {

  /**
   * Reads changes for this widget from the message that has been received from the client.
   * This method is called during the <em>Read Data</em> phase of the request life cycle.
   *
   * @param widget the widget to be processed
   */
  void readData( Widget widget );

  /**
   * Preserves the current state of the widget in order to be able to keep track of changes. This
   * method is called after the <em>Read Data</em> phase but before the <em>Process Action</em>
   * phase of the request life cycle. Thus, the client and server state is in sync. By preserving
   * the current state of the widget, it is later possible to identify the properties that have been
   * modified during the processing of the request.
   * <p>
   * Implementors can use the method {@link IWidgetAdapter#preserve(String, Object)}. As a rule of
   * thumb, every property that is written in <code>render</code> must be preserved in this method.
   * </p>
   *
   * @param widget the widget to be processed
   */
  void preserveValues( Widget widget );

  /**
   * Renders the changes that have been made to this widget to the message that will be sent to the
   * client. This method is called during the <em>Render</em> phase of the request life cycle.
   *
   * @param widget the widget to render changes for
   * @throws IOException
   */
  void render( Widget widget ) throws IOException;

}
