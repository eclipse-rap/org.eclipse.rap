/*******************************************************************************
 * Copyright (c) 2002-2007 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.rwt.lifecycle;

import java.io.IOException;
import org.eclipse.swt.widgets.Widget;


/**
 * A stateless callback handler used by RWT to synchronize the client-side and
 * server-side state of a widget. Each widget type should provide its own
 * implementation of this interface.
 * 
 * @see AbstractWidgetLCA
 * @see WidgetLCAUtil
 * @since 1.0
 */
public interface IWidgetLifeCycleAdapter extends ILifeCycleAdapter {

  /**
   * Preserves the current state of the widget in order to be able to keep track
   * of changes. This method is called after the <em>Read Data</em> phase but
   * before the <em>Process Action</em> phase of the request life cycle. Thus,
   * the client and server state is in sync. By preserving the current state of
   * the widget, it is later possible to identify the properties that have been
   * modified during the processing of the request.
   * <p>
   * Implementors can use the method
   * {@link IWidgetAdapter#preserve(String, Object)}. As a rule of thumb, every
   * property that is written in <code>render</code> must be preserved in this
   * method.
   * </p>
   * 
   * @param widget the widget, the properties of which are preserved
   */
  void preserveValues( Widget widget );
  
  /**
   * Reads request parameters and applies the state changes that are indicated
   * by the client to the widget. This method is called during the
   * <em>Read Data</em> phase of the request life cycle.
   * 
   * @param widget the widget to be processed
   */
  void readData( Widget widget );
  
  /**
   * Writes JavaScript code to the response that renders the changes that has
   * been made to the widget.
   * 
   * @param widget the widget to render changes for
   * @throws IOException
   */
  void render( Widget widget ) throws IOException;
}
