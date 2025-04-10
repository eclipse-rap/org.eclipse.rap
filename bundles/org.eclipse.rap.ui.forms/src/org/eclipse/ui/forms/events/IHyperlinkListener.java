/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.forms.events;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Classes that implement this interface will be notified when hyperlinks are
 * entered, exited and activated.
 * 
 * @see org.eclipse.ui.forms.widgets.Hyperlink
 * @see org.eclipse.ui.forms.widgets.ImageHyperlink
 * @see org.eclipse.ui.forms.widgets.FormText
 * @since 1.0
 */
public interface IHyperlinkListener {
	/**
	 * Sent when hyperlink is entered either by mouse entering the link client area,
	 * or keyboard focus switching to the hyperlink.
	 * 
	 * @param e an event containing information about the hyperlink
	 */
	void linkEntered(HyperlinkEvent e);

	/**
	 * Sent when hyperlink is exited either by mouse exiting the link client area,
	 * or keyboard focus switching from the hyperlink.
	 * 
	 * @param e an event containing information about the hyperlink
	 */
	void linkExited(HyperlinkEvent e);

	/**
	 * Sent when hyperlink is activated either by mouse click inside the link client
	 * area, or by pressing 'Enter' key while hyperlink has keyboard focus.
	 * 
	 * @param e an event containing information about the hyperlink
	 */
	void linkActivated(HyperlinkEvent e);

	/**
	 * Static helper method to create a <code>IHyperlinkListener</code> for the
	 * {@link #linkEntered(HyperlinkEvent)} method, given a lambda expression or a
	 * method reference.
	 *
	 * @param consumer the consumer of the event
	 * @return IHyperlinkListener
	 * @since 4.3
	 */
	static IHyperlinkListener linkEnteredAdapter(Consumer<HyperlinkEvent> consumer) {
		Objects.requireNonNull(consumer);
		return new HyperlinkAdapter() {
			@Override
			public void linkEntered(HyperlinkEvent e) {
				consumer.accept(e);
			}
		};
	}

	/**
	 * Static helper method to create a <code>IHyperlinkListener</code> for the
	 * {@link #linkExited(HyperlinkEvent)} method, given a lambda expression or a
	 * method reference.
	 *
	 * @param consumer the consumer of the event
	 * @return IHyperlinkListener
	 * @since 4.3
	 */
	static IHyperlinkListener linkExitedAdapter(Consumer<HyperlinkEvent> consumer) {
		Objects.requireNonNull(consumer);
		return new HyperlinkAdapter() {
			@Override
			public void linkExited(HyperlinkEvent e) {
				consumer.accept(e);
			}
		};
	}

	/**
	 * Static helper method to create a <code>IHyperlinkListener</code> for the
	 * {@link #linkActivated(HyperlinkEvent)} method, given a lambda expression or a
	 * method reference.
	 *
	 * @param consumer the consumer of the event
	 * @return IHyperlinkListener
	 * @since 4.3
	 */
	static IHyperlinkListener linkActivatedAdapter(Consumer<HyperlinkEvent> consumer) {
		Objects.requireNonNull(consumer);
		return new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				consumer.accept(e);
			}
		};
	}
}
