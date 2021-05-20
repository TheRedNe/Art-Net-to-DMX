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

package de.artnettodmx.portocol;

import java.net.InetAddress;

import ch.bildspur.artnet.ArtNetClient;
import ch.bildspur.artnet.events.ArtNetServerEventAdapter;
import ch.bildspur.artnet.packets.ArtDmxPacket;
import ch.bildspur.artnet.packets.ArtNetPacket;
import ch.bildspur.artnet.packets.PacketType;
import de.artnettodmx.ArtNetToDMX;
import de.artnettodmx.portocol.dmx.UsbDmxClient;

/**
 * @author TheRedNe aka. Nelio Junge
 * @created 17.05.2021
 */
public class ComunicationManager {

	private ArtNetToDMX client;

	private ArtNetClient artNet;
	private UsbDmxClient usbDmx;
	
	private boolean portOpen = false;

	public ComunicationManager(ArtNetToDMX client, InetAddress artNetAdress, String usbDmxComPort, int usbBaudrate, boolean onlyArtNet) {
		this.client = client;
		
		if (!onlyArtNet) {
			this.usbDmx = new UsbDmxClient(usbDmxComPort, usbBaudrate);
			this.portOpen = this.usbDmx.getSerialPort().isOpen();
		}
			
		this.artNet = new ArtNetClient();
		this.artNet.start(artNetAdress);

		if (!this.artNet.isRunning()) {
			System.err.println("[Communication-Manager] Error while starting Art-Net! Shutting down...");
			stop();
			return;
		}

		System.out.println("[Communication-Manager] Started connection!");

		this.artNet.getArtNetServer().addListener(new ArtNetServerEventAdapter() {
			@Override
			public void artNetPacketReceived(ArtNetPacket packet) {
				if (packet.getType().equals(PacketType.ART_OUTPUT)) {
					ArtDmxPacket dmxPacket = (ArtDmxPacket) packet;
					ComunicationManager.this.client.artNetRecived(dmxPacket);
					if (!onlyArtNet && portOpen)
						ComunicationManager.this.usbDmx.sendPacket(dmxPacket);
				}
			}
		});
	}

	public void stop() {
		if (this.artNet != null)
			this.artNet.stop();
		if (this.usbDmx != null)
			this.usbDmx.stop();
		System.out.println("[Communication-Manager] Stopped connection!");
	}
}
