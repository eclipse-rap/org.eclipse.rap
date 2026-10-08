// RAP [bm]: Dnd / Mouse
///*******************************************************************************
// * Copyright (c) 2004, 2005 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License 2.0
// * which is available at
// * https://www.eclipse.org/legal/epl-2.0
// *
// * SPDX-License-Identifier: EPL-2.0
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *******************************************************************************/
//package org.eclipse.ui.internal.presentations;
//
//import org.eclipse.jface.action.Action;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.ui.internal.WorkbenchMessages;
//import org.eclipse.ui.presentations.IPresentablePart;
//import org.eclipse.ui.presentations.IStackPresentationSite;
//
///**
// * @since 3.0
// */
//public class SystemMenuMovePane extends Action {
//
//    IStackPresentationSite site;
//    IPresentablePart part;
//
//    public SystemMenuMovePane(IStackPresentationSite site) {
//        this.site = site;
//        setText(WorkbenchMessages.get().SystemMenuMovePane_PaneName);
//    }
//
//    /* (non-Javadoc)
//     * @see org.eclipse.jface.action.IAction#run()
//     */
//    public void run() {
//        site.dragStart(part, Display.getDefault().getCursorLocation(), true);
//    }
//
//    public void setTarget(IPresentablePart part) {
//        this.part = part;
//        setEnabled(part != null && site.isPartMoveable(part));
//    }
//
//}
