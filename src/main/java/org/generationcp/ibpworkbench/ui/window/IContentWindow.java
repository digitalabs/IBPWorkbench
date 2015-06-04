/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.ibpworkbench.ui.window;

import com.vaadin.ui.Component;

/**
 * An {@link IContentWindow} is a window that provides a "content area".
 *
 * @author Glenn Marintes
 */
public interface IContentWindow {

	public void showContent(Component content);

	public void showContent(String toolUrl);

}
