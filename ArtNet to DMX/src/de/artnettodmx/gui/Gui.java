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

package de.artnettodmx.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.artnettodmx.gui.popups.ShowOutputPopup;

/**
 * @author TheRedNe aka. Nelio Junge
 * @created 17.05.2021
 */
@SuppressWarnings("serial")
public class Gui extends JFrame {
	public JMenu showOutputMenu;
	public ShowOutputPopup outputPopup;

	public Gui(String title, int width, int height, WindowAdapter closeAdapter) {
		this.setTitle(title);
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setFocusable(true);
		this.setLayout(new FlowLayout());
		this.setResizable(false);
		this.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("24x24.png")).getImage());
		this.setLocationRelativeTo(null);
		this.addWindowListener(closeAdapter);

		JMenuBar menuBar = new JMenuBar();
		menuBar.setBackground(Color.WHITE);
		menuBar.setForeground(Color.BLACK);
		this.setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");

		JMenuItem exitItem = new JMenuItem("Exit                   ");
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Gui.this.showOutputMenu.isEnabled()) {
					closeAdapter.windowClosing(new WindowEvent(Gui.this, WindowEvent.WINDOW_CLOSING));
				}
			}
		});
		fileMenu.add(exitItem);

		menuBar.add(fileMenu);

		this.showOutputMenu = new JMenu("Show Output");
		this.showOutputMenu.addMouseListener(new ClickListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (Gui.this.showOutputMenu.isEnabled()) {
					Gui.this.outputPopup = new ShowOutputPopup(Gui.this);
					Gui.this.showOutputMenu.setEnabled(false);
				}
			}
		});
		menuBar.add(this.showOutputMenu);

		JTextArea console = new JTextArea("");
		console.setLineWrap(false);
		console.setBackground(new Color(170, 170, 170));
		console.setForeground(Color.BLACK);
		console.setEditable(false);

		ConsolePrintStream out = new ConsolePrintStream(new ConsoleOutputStream(console, false));
		ConsolePrintStream err = new ConsolePrintStream(new ConsoleOutputStream(console, true));

		JScrollPane scroller = new JScrollPane(console);
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.setPreferredSize(new Dimension(width - 20, height - 70));
		this.add(scroller);

		System.setOut(out);
		System.setErr(err);

		this.setVisible(true);
	}

	private class ConsoleOutputStream extends OutputStream {

		private boolean isErrorStream = false;

		private JTextArea console;

		public ConsoleOutputStream(JTextArea console, boolean isError) {
			this.isErrorStream = isError;
			this.console = console;
		}

		@Override
		public void write(int b) throws IOException {
		}

		@Override
		public void write(byte[] b) throws IOException {
			write(b, 0, b.length);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			} else if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return;
			}
			byte[] bs = new byte[len];
			for (int i = 0; i < len; i++) {
				bs[i] = b[i + off];
			}
			String s = new String(bs);
			if (this.isErrorStream) {
				this.console.append(String.format("[ERROR] %s \n", s));
			} else {
				this.console.append(String.format("[INFO] %s \n", s));
			}
		}

		@Override
		public void close() throws IOException {
		}

		@Override
		public void flush() throws IOException {
		}
	}

	private class ConsolePrintStream extends PrintStream {

		public ConsolePrintStream(ConsoleOutputStream out) {
			super(out);
		}

		@Override
		public void println() {
		}

		@Override
		public void println(boolean x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(char x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(char[] x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(double x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(float x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(int x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(long x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(Object x) {
			synchronized (this) {
				super.print(x);
			}
		}

		@Override
		public void println(String x) {
			synchronized (this) {
				super.print(x);
			}
		}
	}

	private abstract class ClickListener implements MouseListener {

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}
}
