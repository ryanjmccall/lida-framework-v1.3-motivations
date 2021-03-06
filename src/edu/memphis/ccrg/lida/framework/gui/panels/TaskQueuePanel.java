/*******************************************************************************
 * Copyright (c) 2009, 2011 The University of Memphis.  All rights reserved. 
 * This program and the accompanying materials are made available 
 * under the terms of the LIDA Software Framework Non-Commercial License v1.0 
 * which accompanies this distribution, and is available at
 * http://ccrg.cs.memphis.edu/assets/papers/2010/LIDA-framework-non-commercial-v1.0.pdf
 *******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PropertiesPanel.java 
 *
 * Created on 14/08/2009, 13:37:17
 */
package edu.memphis.ccrg.lida.framework.gui.panels;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import edu.memphis.ccrg.lida.framework.tasks.FrameworkTask;
import edu.memphis.ccrg.lida.framework.tasks.TaskManager;

/**
 * A {@link GuiPanel} which displays the current queue of tasks scheduled for
 * execution in {@link TaskManager}. Rows represent all the tasks scheduled at a
 * particular tick . The first column is the tick number and the rest of the
 * columns are for each individual tasks scheduled at a particular tick.
 * 
 * @author Javier Snaider
 */
public class TaskQueuePanel extends GuiPanelImpl {

	private static final Logger logger = Logger.getLogger(TaskQueuePanel.class
			.getCanonicalName());
	private Map<Long, Set<FrameworkTask>> tasks = new HashMap<Long, Set<FrameworkTask>>();

	// TODO xml support
	private int DEFAULT_COLUMN_WIDTH = 150;
	private int columnWidth = DEFAULT_COLUMN_WIDTH;

	/**
	 * Default constructor
	 */
	public TaskQueuePanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jToolBar1 = new javax.swing.JToolBar();
		refreshButton = new javax.swing.JButton();
		taskPane = new javax.swing.JScrollPane();
		taskTable = new javax.swing.JTable();

		jToolBar1.setRollover(true);

		refreshButton.setText("Refresh");
		refreshButton.setFocusable(false);
		refreshButton
				.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
		refreshButton
				.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
		refreshButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshButtonActionPerformed(evt);
			}
		});
		jToolBar1.add(refreshButton);

		taskTable.setModel(new TaskQueueTableModel());
		taskTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
		taskTable.setMaximumSize(new java.awt.Dimension(1000, 1000));
		taskPane.setViewportView(taskTable);
		taskTable.setColumnModel(new TaskColumnTableModel(columnWidth));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(
				javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				taskPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400,
				Short.MAX_VALUE).addComponent(jToolBar1,
				javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addComponent(
												jToolBar1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												25,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(
												taskPane,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												215, Short.MAX_VALUE)));
	}// </editor-fold>//GEN-END:initComponents

	private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshButtonActionPerformed
		refresh();
	}// GEN-LAST:event_refreshButtonActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JToolBar jToolBar1;
	private javax.swing.JButton refreshButton;
	private javax.swing.JScrollPane taskPane;
	private javax.swing.JTable taskTable;

	// End of variables declaration//GEN-END:variables

	/*
	 * Implementation of abstract table model to display the contents of the
	 * TaskManager's task queue to a Table.
	 * 
	 * @author Javier Snaider
	 */
	private class TaskQueueTableModel extends AbstractTableModel {

		/**
		 * Returns the size of the largest queue in the task queue plus 1
		 * 
		 * @see javax.swing.table.TableModel#getColumnCount()
		 */
		@Override
		public int getColumnCount() {
			int total = 0;
			for (Set<FrameworkTask> qt : tasks.values()) {
				if (qt.size() > total) {
					total = qt.size();
				}
			}
			return total + 1; // the first one is the tick number
		}

		@Override
		public int getRowCount() {
			int rows = (int) (agent.getTaskManager().getMaxTick() - TaskManager
					.getCurrentTick()) + 1;
			return rows;
		}

		@Override
		public String getColumnName(int column) {
			String cName;
			if (column == 0) {
				cName = "Scheduled Tick";
			} else {
				cName = "Task " + column;
			}
			return cName;
		}

		/**
		 * Based on the specified indices, returns a FrameworkTask.
		 * 
		 * @param rowIndex
		 *            scheduled tick of the FrameworkTask
		 * @param columnIndex
		 *            the position of the FrameworkTask in the rowIndex row.
		 * @see javax.swing.table.TableModel#getValueAt(int, int)
		 */
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			Object o = null;
			if (columnIndex == 0) {
				return TaskManager.getCurrentTick() + rowIndex;
			}

			Set<FrameworkTask> qt = tasks.get(TaskManager.getCurrentTick()
					+ rowIndex);
			if (qt == null) {
				return "";
			}
			Iterator<FrameworkTask> it = qt.iterator();
			for (int i = 1; i <= columnIndex; i++) {
				if (it.hasNext()) {
					o = it.next();
				} else {
					o = "";
					break;
				}
			}
			return o;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}

	@Override
	public void refresh() {
		logger.log(Level.FINEST, "Refreshing TaskQueue Panel", TaskManager
				.getCurrentTick());
		display(agent.getTaskManager().getTaskQueue());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void display(Object o) {
		if (o instanceof Map) {
			tasks = (Map<Long, Set<FrameworkTask>>) o;
			((AbstractTableModel) taskTable.getModel())
					.fireTableStructureChanged();
		}
	}

	private class TaskColumnTableModel extends DefaultTableColumnModel {

		private int width;

		public TaskColumnTableModel(int width) {
			this.width = width;
		}

		@Override
		public void addColumn(TableColumn column) {
			column.setPreferredWidth(width);
			super.addColumn(column);
		}
	}
}
