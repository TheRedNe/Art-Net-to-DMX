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

package de.artnettodmx;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;

import ch.bildspur.artnet.packets.ArtDmxPacket;
import de.artnettodmx.gui.Gui;
import de.artnettodmx.portocol.ComunicationManager;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

/**
 * @author TheRedNe aka. Nelio Junge
 * @created 17.05.2021
 */
public class ArtNetToDMX {

	private ComunicationManager comManager;
	private Gui gui;
	
	private String artNetAdress;
	private boolean showReceive;
	private boolean onlyArtNet;
	private String usbComPort;
	private int baudrate;

	public static void main(String[] args) {
		OptionParser optionParser = new OptionParser();
		OptionSpec<String> optionspec1 = optionParser.accepts("artnetaddress").withRequiredArg().defaultsTo("2.100.100.1");
		OptionSpec<String> optionspec2 = optionParser.accepts("usbcomport").withRequiredArg().defaultsTo("COM9");
		OptionSpec<Integer> optionspec3 = optionParser.accepts("usbbaudrate").withRequiredArg().<Integer>ofType(Integer.class).defaultsTo(250000);
		OptionSpec<Void> optionspec4 = optionParser.accepts("nogui");
		OptionSpec<Void> optionspec5 = optionParser.accepts("showReceive");
		OptionSpec<Void> optionspec6 = optionParser.accepts("withoutusb");
		OptionSpec<String> optionspec7 = optionParser.nonOptions();
		OptionSet optionset = optionParser.parse(args);
		
		new ArtNetToDMX(optionset.valueOf(optionspec1), optionset.valueOf(optionspec2), ((Integer)optionset.valueOf(optionspec3)).intValue(), optionset.has(optionspec4), optionset.has(optionspec5), optionset.has(optionspec6), optionset.valuesOf(optionspec7));
	}

	public ArtNetToDMX(String artNetAdress, String usbComPort, int baudrate, boolean nogui, boolean showReceive, boolean onlyArtNet, List<String> list) {
		this.artNetAdress = artNetAdress;
		this.usbComPort = usbComPort;
		this.baudrate = baudrate;
		this.showReceive = showReceive;
		this.onlyArtNet = onlyArtNet;
		
		if (!nogui) {
			this.gui = new Gui("to DMX by TheRedNe", 400, 450, new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					if (ArtNetToDMX.this.comManager != null)
						ArtNetToDMX.this.comManager.stop();
					Runtime.getRuntime().exit(0);
				}
			});
		}
		
		if (!list.isEmpty()) {
			System.out.println("[Args-Parser] Completely ignored arguments: " + list);
		}
		
		System.out.println("Available serial-ports: ");
		for (SerialPort port : SerialPort.getCommPorts())
			System.out.println(" - PortName: " + port.getSystemPortName() + " Description: " + port.getPortDescription() + " DescriptivePortName: " + port.getDescriptivePortName());

		
		start();
	}

	public void start() {
		try {
			this.comManager = new ComunicationManager(this, InetAddress.getByName(this.artNetAdress), this.usbComPort,
					this.baudrate, this.onlyArtNet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		if (this.comManager != null)
			this.comManager.stop();
	}

	public void artNetRecived(ArtDmxPacket data) {
		if (this.showReceive)
			System.out.println(String.format("[Communication-Manager] Received Art-Net Dmx data (Size: %s) ", data.getLength()));
		if (this.gui != null) {
			if (this.gui.outputPopup != null) {
				this.gui.outputPopup.data = data.getDmxData();
				this.gui.outputPopup.outputTable.repaint();
			}
		}
	}
}
