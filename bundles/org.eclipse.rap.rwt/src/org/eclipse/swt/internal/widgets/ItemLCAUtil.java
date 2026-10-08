/*******************************************************************************
 * Copyright (c) 2002, 2014 Innoopract Informationssysteme GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Innoopract Informationssysteme GmbH - initial API and implementation
 *    EclipseSource - ongoing development
 ******************************************************************************/
package org.eclipse.swt.internal.widgets;

import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.preserveProperty;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderData;
import static org.eclipse.rap.rwt.internal.lifecycle.WidgetLCAUtil.renderProperty;

import org.eclipse.swt.widgets.Item;


public class ItemLCAUtil {

  private static final String PROP_TEXT = "text";
  private static final String PROP_IMAGE = "image";

  private ItemLCAUtil() {
    // prevent instantiation
  }

  public static void preserve( Item item ) {
    preserveProperty( item, PROP_TEXT, item.getText() );
    preserveProperty( item, PROP_IMAGE, item.getImage() );
  }

  public static void renderChanges( Item item ) {
    renderProperty( item, PROP_TEXT, item.getText(), "" );
    renderProperty( item, PROP_IMAGE, item.getImage(), null );
    renderData( item );
  }

}
