/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
package edu.memphis.ccrg.lida.actionselection;

import edu.memphis.ccrg.lida.framework.shared.NodeStructure;

/**
 * Listener of preafferent signal from {@link ActionSelection}
 * 
 * @author Ryan J. McCall
 * 
 */
public interface PreafferenceListener {

	/**
	 * Listener receives preafference.
	 * 
	 * @param addSet
	 *            expected additions in future percepts
	 * @param deleteSet
	 *            expected deletions in future percepts
	 */
	public void receivePreafference(NodeStructure addSet,
			NodeStructure deleteSet);

}
