/*******************************************************************************
 * Copyright (c) 2009, 2015 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package org.eclipse.rap.rwt.internal.theme;

import org.eclipse.rap.rwt.internal.theme.css.ConditionalValue;
import org.eclipse.swt.widgets.Widget;


/**
 * Instances of this class can be used to select a value from a conditional
 * values array.
 */
public interface ValueSelector {

  CssValue select( Widget widget, ConditionalValue... values );

}
