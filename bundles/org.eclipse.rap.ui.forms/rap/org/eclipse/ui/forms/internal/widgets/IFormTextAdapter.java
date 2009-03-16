/*******************************************************************************
 * Copyright (c) 2009 Innoopract Informationssysteme GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Innoopract Informationssysteme GmbH - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui.forms.internal.widgets;

import java.util.Hashtable;

import org.eclipse.ui.internal.forms.widgets.Paragraph;

public interface IFormTextAdapter {

  Paragraph[] getParagraphs();
  Hashtable getResourceTable();
  boolean hasLayoutChanged();

}
