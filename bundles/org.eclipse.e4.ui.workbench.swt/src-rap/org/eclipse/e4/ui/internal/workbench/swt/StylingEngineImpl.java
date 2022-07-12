/*******************************************************************************
 * Copyright (c) 2022 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/

package org.eclipse.e4.ui.internal.workbench.swt;

import java.util.Arrays;
import java.util.List;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.css.CSSStyleDeclaration;

public class StylingEngineImpl implements IStylingEngine {

	@Override
	public void setClassname(Object widget, String classname) {
	}

	@Override
	public void setId(Object widget, String id) {
	}

	@Override
	public void setClassnameAndId(Object widget, String classname, String id) {
		List<String> parts = Arrays.asList(classname.split(" "));
		if (parts.contains("MPartStack")) {
			if (widget instanceof CTabFolder) {
				CTabFolder folder = (CTabFolder) widget;
				Display display = folder.getDisplay();
				if (parts.contains("active")) {
					folder.setSelectionBackground(display.getSystemColor(SWT.COLOR_LIST_SELECTION));
				} else {
					folder.setSelectionBackground((Color) null);
				}
			}
		}
	}

	@Override
	public void style(Object widget) {
	}

	@Override
	public CSSStyleDeclaration getStyle(Object widget) {
		return null;
	}

}
