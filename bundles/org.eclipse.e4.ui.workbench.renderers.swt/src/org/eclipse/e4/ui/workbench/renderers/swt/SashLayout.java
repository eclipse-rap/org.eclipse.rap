/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.e4.ui.workbench.renderers.swt;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.e4.ui.model.application.ui.MGenericTile;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;

public class SashLayout extends Layout {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	// The minimum value (as a percentage) that a sash can be dragged to
	int minSashPercent = 10;

	int marginLeft = 0;
	int marginRight = 0;
	int marginTop = 0;
	int marginBottom = 0;
	int sashWidth = 4;

	MUIElement root;
	private Composite host;

	class SashRect {
		Rectangle rect;
		MGenericTile<?> container;
		MUIElement left;
		MUIElement right;

		public SashRect(Rectangle rect, MGenericTile<?> container,
				MUIElement left, MUIElement right) {
			this.container = container;
			this.rect = rect;
			this.left = left;
			this.right = right;
		}
	}

	List<SashRect> sashes = new ArrayList<SashRect>();

	boolean draggingSashes = false;
	List<SashRect> sashesToDrag;

	public boolean layoutUpdateInProgress = false;

	public SashLayout(final Composite host, MUIElement root) {
		this.root = root;
		this.host = host;
		// RAP does not support mouse track/move listeners
		// host.addMouseTrackListener(new MouseTrackListener() {
		// @Override
		// public void mouseHover(MouseEvent e) {
		// }
		//
		// @Override
		// public void mouseExit(MouseEvent e) {
		// host.setCursor(null);
		// }
		//
		// @Override
		// public void mouseEnter(MouseEvent e) {
		// }
		// });
		//
		// host.addMouseMoveListener(new MouseMoveListener() {
		// @Override
		// public void mouseMove(final MouseEvent e) {
		// if (!draggingSashes) {
		// // Set the cursor feedback
		// List<SashRect> sashList = getSashRects(e.x, e.y);
		// if (sashList.size() == 0) {
		// host.setCursor(host.getDisplay().getSystemCursor(
		// SWT.CURSOR_ARROW));
		// } else if (sashList.size() == 1) {
		// if (sashList.get(0).container.isHorizontal())
		// host.setCursor(host.getDisplay().getSystemCursor(
		// SWT.CURSOR_SIZEWE));
		// else
		// host.setCursor(host.getDisplay().getSystemCursor(
		// SWT.CURSOR_SIZENS));
		// } else {
		// host.setCursor(host.getDisplay().getSystemCursor(
		// SWT.CURSOR_SIZEALL));
		// }
		// } else {
		// try {
		// layoutUpdateInProgress = true;
		// adjustWeights(sashesToDrag, e.x, e.y);
		// host.layout();
		// host.update();
		// } finally {
		// layoutUpdateInProgress = false;
		// }
		// }
		// }
		// });
		//
		// host.addMouseListener(new MouseListener() {
		// @Override
		// public void mouseUp(MouseEvent e) {
		// host.setCapture(false);
		// draggingSashes = false;
		// }
		//
		// @Override
		// public void mouseDown(MouseEvent e) {
		// if (e.button != 1) {
		// return;
		// }
		//
		// sashesToDrag = getSashRects(e.x, e.y);
		// if (sashesToDrag.size() > 0) {
		// draggingSashes = true;
		// host.setCapture(true);
		// }
		// }
		//
		// @Override
		// public void mouseDoubleClick(MouseEvent e) {
		// }
		// });
		//
		// host.addPaintListener(new PaintListener() {
		// @Override
		// public void paintControl(PaintEvent e) {
		// // for (SashRect sr : sashes) {
		// // Color color;
		// // if (sr.container.isHorizontal())
		// // color = e.display.getSystemColor(SWT.COLOR_MAGENTA);
		// // else
		// // color = e.display.getSystemColor(SWT.COLOR_CYAN);
		// // e.gc.setForeground(color);
		// // e.gc.setBackground(color);
		// // e.gc.fillRectangle(sr.rect);
		// // }
		// }
		// });
	}

	@Override
	protected void layout(Composite composite, boolean flushCache) {
		if (root == null)
			return;

		Rectangle bounds = composite.getBounds();
		if (composite instanceof Shell)
			bounds = ((Shell) composite).getClientArea();
		else {
			bounds.x = 0;
			bounds.y = 0;
		}

		bounds.width -= (marginLeft + marginRight);
		bounds.height -= (marginTop + marginBottom);
		bounds.x += marginLeft;
		bounds.y += marginTop;

		sashes.clear();
		tileSubNodes(bounds, root);
		// RAP: Use real Sash widgets instead of mouse track/move listeners
		updateSashWidgets();
		// RAPEND
	}

	// RAP: Use real Sash widgets instead of mouse track/move listeners
	List<Sash> sashWidgets = new ArrayList<Sash>();
	private static final String SELECTION_LISTENER_ID = "selListener"; //$NON-NLS-1$

	private void updateSashWidgets() {
		for (int i = 0; i < sashes.size(); i++) {
			SashRect sashRect = sashes.get(i);
			if (i < sashWidgets.size()) {
				Sash sashWidget = sashWidgets.get(i);
				if (isSameOrientation(sashWidget, sashRect)) {
					sashWidget.setBounds(sashRect.rect);
					sashWidget.removeListener(SWT.Selection, (Listener) sashWidget.getData(SELECTION_LISTENER_ID));

					SelectionListener selListener = new SelectionListener(sashRect, sashWidget);
					sashWidget.setData(SELECTION_LISTENER_ID, selListener);
					sashWidget.addListener(SWT.Selection, selListener);
				} else {
					sashWidgets.set(i, createSash(sashRect)).dispose();
				}
			} else {
				sashWidgets.add(createSash(sashRect));
			}
		}
		while (sashWidgets.size() > sashes.size()) {
			sashWidgets.remove(sashWidgets.size() - 1).dispose();
		}
	}

	class SelectionListener implements Listener {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		SashRect sashRect;
		Sash sash;

		public SelectionListener(SashRect sr, Sash sash) {
			sashRect = sr;
			this.sash = sash;
		}

		@Override
		public void handleEvent(Event event) {
			Display display = host.getDisplay();
			Point cursorLocation = display.getCursorLocation();
			Point mapped = display.map(null, host, cursorLocation);
			List<SashRect> sashesToDrag = new ArrayList<SashLayout.SashRect>();
			sashesToDrag.add(sashRect);
			adjustWeights(sashesToDrag, mapped.x, mapped.y);
			host.layout();
			host.update();
		}
	}

	private Sash createSash(final SashRect sashRect) {
		boolean horizontal = !sashRect.container.isHorizontal();
		Sash sash = new Sash(host, horizontal ? SWT.HORIZONTAL : SWT.VERTICAL);
		sash.setBounds(sashRect.rect);

		SelectionListener selListener = new SelectionListener(sashRect, sash);
		sash.setData(SELECTION_LISTENER_ID, selListener);
		sash.addListener(SWT.Selection, selListener);
		return sash;
	}

	private boolean isSameOrientation(Sash sash, SashRect sashRect) {
		boolean isSashHorizontal = (sash.getStyle() & SWT.HORIZONTAL) != 0;
		boolean isSashContainerHorizontal = sashRect.container.isHorizontal();
		return isSashHorizontal != isSashContainerHorizontal;
	}
	// RAPEND

	protected void adjustWeights(List<SashRect> sashes, int curX, int curY) {
		for (SashRect sr : sashes) {
			int totalWeight = getWeight(sr.left) + getWeight(sr.right);
			int minSashValue = (int) (((totalWeight / 100.0) * minSashPercent) + 0.5);

			Rectangle leftRect = getRectangle(sr.left);
			Rectangle rightRect = getRectangle(sr.right);
			if (leftRect == null || rightRect == null)
				continue;

			int leftWeight;
			int rightWeight;

			if (sr.container.isHorizontal()) {
				double left = leftRect.x;
				double right = rightRect.x + rightRect.width;
				double pct = (curX - left) / (right - left);
				leftWeight = (int) ((totalWeight * pct) + 0.5);
				if (leftWeight < minSashValue)
					leftWeight = minSashValue;
				if (leftWeight > (totalWeight - minSashValue))
					leftWeight = totalWeight - minSashValue;
				rightWeight = totalWeight - leftWeight;
			} else {
				double top = leftRect.y;
				double bottom = rightRect.y + rightRect.height;
				double pct = (curY - top) / (bottom - top);
				leftWeight = (int) ((totalWeight * pct) + 0.5);
				if (leftWeight < minSashValue)
					leftWeight = minSashValue;
				if (leftWeight > (totalWeight - minSashValue))
					leftWeight = totalWeight - minSashValue;
				rightWeight = totalWeight - leftWeight;
			}

			setWeight(sr.left, leftWeight);
			setWeight(sr.right, rightWeight);
		}
	}

	private void setWeight(MUIElement element, int weight) {
		element.setContainerData(Integer.toString(weight));
	}

	private Rectangle getRectangle(MUIElement element) {
		if (element.getWidget() instanceof Rectangle)
			return (Rectangle) element.getWidget();
		else if (element.getWidget() instanceof Control)
			return ((Control) (element.getWidget())).getBounds();
		return null;
	}

	protected List<SashRect> getSashRects(int x, int y) {
		List<SashRect> srs = new ArrayList<SashRect>();
		Rectangle target = new Rectangle(x - 5, y - 5, 10, 10);
		for (SashRect sr : sashes) {
			if (!sr.container.getTags().contains(IPresentationEngine.NO_MOVE)
					&& sr.rect.intersects(target))
				srs.add(sr);
		}
		return srs;
	}

	@Override
	protected Point computeSize(Composite composite, int wHint, int hHint,
			boolean flushCache) {
		return new Point(600, 400);
	}

	private int totalWeight(MGenericTile<?> node) {
		int total = 0;
		for (MUIElement subNode : node.getChildren()) {
			if (subNode.isToBeRendered() && subNode.isVisible())
				total += getWeight(subNode);
		}
		return total;
	}

	private void tileSubNodes(Rectangle bounds, MUIElement node) {
		if (node != root)
			setRectangle(node, bounds);

		if (!(node instanceof MGenericTile<?>))
			return;

		MGenericTile<?> sashContainer = (MGenericTile<?>) node;
		List<MUIElement> visibleChildren = getVisibleChildren(sashContainer);
		int childCount = visibleChildren.size();

		// How many pixels do we have?
		int availableWidth = sashContainer.isHorizontal() ? bounds.width
				: bounds.height;

		// Subtract off the room for the sashes
		availableWidth -= ((childCount - 1) * sashWidth);

		// Get the total of the weights
		double totalWeight = totalWeight(sashContainer);
		int tilePos = sashContainer.isHorizontal() ? bounds.x : bounds.y;

		MUIElement prev = null;
		for (MUIElement subNode : visibleChildren) {
			// Add a 'sash' between this node and the 'prev'
			if (prev != null) {
				Rectangle sashRect = sashContainer.isHorizontal() ? new Rectangle(
						tilePos, bounds.y, sashWidth, bounds.height)
						: new Rectangle(bounds.x, tilePos, bounds.width,
								sashWidth);
				sashes.add(new SashRect(sashRect, sashContainer, prev, subNode));
				host.redraw(sashRect.x, sashRect.y, sashRect.width,
						sashRect.height, false);
				tilePos += sashWidth;
			}

			// Calc the new size as a %'age of the total
			double ratio = getWeight(subNode) / totalWeight;
			int newSize = (int) ((availableWidth * ratio) + 0.5);

			Rectangle subBounds = sashContainer.isHorizontal() ? new Rectangle(
					tilePos, bounds.y, newSize, bounds.height) : new Rectangle(
					bounds.x, tilePos, bounds.width, newSize);
			tilePos += newSize;

			tileSubNodes(subBounds, subNode);
			prev = subNode;
		}
	}

	/**
	 * @param node
	 * @param bounds
	 */
	private void setRectangle(MUIElement node, Rectangle bounds) {
		if (node.getWidget() instanceof Control) {
			Control ctrl = (Control) node.getWidget();
			ctrl.setBounds(bounds);
		} else if (node.getWidget() instanceof Rectangle) {
			Rectangle theRect = (Rectangle) node.getWidget();
			theRect.x = bounds.x;
			theRect.y = bounds.y;
			theRect.width = bounds.width;
			theRect.height = bounds.height;
		}
	}

	private List<MUIElement> getVisibleChildren(MGenericTile<?> sashContainer) {
		List<MUIElement> visKids = new ArrayList<MUIElement>();
		for (MUIElement child : sashContainer.getChildren()) {
			if (child.isToBeRendered() && child.isVisible())
				visKids.add(child);
		}
		return visKids;
	}

	private static int getWeight(MUIElement element) {
		String info = element.getContainerData();
		if (info == null || info.length() == 0) {
			return 0;
		}

		try {
			int value = Integer.parseInt(info);
			return value;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
