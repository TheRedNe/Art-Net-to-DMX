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

package de.artnettodmx.portocol.dmx;

import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

import ch.bildspur.artnet.packets.ArtDmxPacket;

/**
 * @author TheRedNe aka. Nelio Junge
 * @created 17.05.2021
 */
public class UsbDmxClient {

	public static final byte DMX_MESSAGE_START = (byte) 0x7E;
	public static final byte DMX_MESSAGE_END = (byte) 0xE7;
	public static final byte DMX_SEND_PACKET = (byte) 6;
	public static final byte DMX_NULL_VALUE = (byte) 0;
	public static final int DMX_DATA_OFFSET = 5;
	public static final int CHANNEL_COUNT = 512;

	private byte[] message = new byte[DMX_DATA_OFFSET + CHANNEL_COUNT + 1];
	private byte[] dmxStates = new byte[CHANNEL_COUNT];

	private SerialPort port;
	private OutputStream serialOut;

	public UsbDmxClient(String comPort, int baudrate) {
		this.port = SerialPort.getCommPort(comPort);
		this.port.setComPortParameters(baudrate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY, false);
		this.port.openPort();

		if (this.port.isOpen()) {
			System.out.println(String.format("[Serial-Controller] (%s) is now open!", this.port.getSystemPortName()));
		} else {
			this.port.closePort();
			System.err.println(String.format("[Serial-Controller] (%s) error while opening port!", this.port.getSystemPortName()));
			return;
		}
		
		this.message[0] = DMX_MESSAGE_START;
		this.message[1] = DMX_SEND_PACKET;
		this.message[2] = 1;
		this.message[3] = 2;
		this.message[4] = 0;
		this.message[DMX_DATA_OFFSET + CHANNEL_COUNT] = DMX_MESSAGE_END;

		this.serialOut = this.port.getOutputStream();
	}

	public void sendPacket(ArtDmxPacket packet) {
		byte[] data = packet.getDmxData();
		try {
			boolean change = false;
			for (int i = 0; i < CHANNEL_COUNT; i++) {
				if (data[i] != this.dmxStates[i]) {
					change = true;
					break;
				} 
			}
			if (change) {
				System.arraycopy(data, 0, this.message, 5, CHANNEL_COUNT);
				System.arraycopy(data, 0, this.dmxStates, 0, CHANNEL_COUNT);
				this.serialOut.write(this.message);
			}
		} catch (Exception e) {
			System.err.println(String.format("[Serial-Controller] (%s) Error while sending data to DMX-Interface! \n %s", this.port.getSystemPortName(), e));
		}
	}

	public void stop() {
		this.port.closePort();
		System.out.println(String.format("[Serial-Controller] (%s) Stopped USB-DMX client!", this.port.getSystemPortName()));
	}

	public SerialPort getSerialPort() {
		return this.port;
	}
}