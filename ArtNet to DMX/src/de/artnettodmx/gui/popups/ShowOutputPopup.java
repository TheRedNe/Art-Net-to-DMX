/*
 * ******************************************************************************
 *  * Copyright (C) 2021, Nelio Junge
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  *
 *  * 1. Redistributions of source code must retain the above copyright notice,
 *  *    this list of conditions and the following disclaimer.
 *  *
 *  * 2. Redistributions in binary form must reproduce the above copyright notice,
 *  *    this list of conditions and the following disclaimer in the documentation
 *  *    and/or other materials provided with the distribution.
 *  *
 *  * 3. Neither the name of the copyright holder nor the names of its contributors
 *  *    may be used to endorse or promote products derived from this software
 *  *    without specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 *  * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 *  * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 *  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *  * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *  * POSSIBILITY OF SUCH DAMAGE.
 *  *****************************************************************************
 */

package de.artnettodmx.gui.popups;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import de.artnettodmx.gui.Gui;
import de.artnettodmx.portocol.dmx.UsbDmxClient;

/**
 * @author TheRedNe aka. Nelio Junge
 * @created 17.05.2021
 */
@SuppressWarnings("serial")
public class ShowOutputPopup extends JFrame {
	public byte[] data = new byte[UsbDmxClient.CHANNEL_COUNT];
	
	public JTable outputTable;
	
	public ShowOutputPopup(Gui gui) {
		this.setTitle("Show Output");
		this.setSize(400, 450);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setFocusable(true);
		this.setLayout(new FlowLayout());
		this.setResizable(false);
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("24x24.png")).getImage());
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				gui.showOutputMenu.setEnabled(true);
			}
		});
		
		this.outputTable = new JTable(new Model());
		this.outputTable.getTableHeader().setReorderingAllowed(false);
		this.outputTable.getTableHeader().setResizingAllowed(false);
		
		JScrollPane scroller = new JScrollPane(this.outputTable);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setPreferredSize(new Dimension(this.getWidth() - 20, this.getHeight() - 45));
		this.add(scroller);
		
		this.setVisible(true);
	}
	
	private class Model implements TableModel {

		@Override
		public int getRowCount() {
			return 52;
		}

		@Override
		public int getColumnCount() {
			return 11;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return columnIndex == 0 ? "Addr" : Integer.toString(columnIndex - 1);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return false;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return rowIndex + "0";
			}
			int index = ((columnIndex - 1) + (10 * rowIndex));

			if (index > 512)
				return 0;
			if (index <= 0) 
				return 0;
			
			return Byte.toUnsignedInt(ShowOutputPopup.this.data[index - 1]);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {	
		}

		@Override
		public void addTableModelListener(TableModelListener l) {
		}

		@Override
		public void removeTableModelListener(TableModelListener l) {
		}
	}
}