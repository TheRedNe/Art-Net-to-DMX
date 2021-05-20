# Art-Net-to-DMX
An Art-Net to DMX programm

I'm sorry for my bad english.

**Usage:**
  - The .jar file will be executed in Command Prompt, Windows PowerShell or Terminal with the following arguments. 
  - You can also execute it with a batch file (.bat), a shell-script (.sh) or a command file (.command).
  
**Art-Net Setup:** 
  - To set the address of the Art-Net Node, you have add the argument *--artnetaddress* and after it the Address, default its 2.100.100.1
  
**Usb-DMX Setup:**
  - To set the serial port where your interface is plugged into your computer, you have to add the argument *--usbcomport* and after it the ports name. Example: COM9 (Windows), /dev/tty.usbserial* (Mac OSX) The default serial port is COM9. If you are not shure to wich serial port your interface is connected you can start the programm and there will be printed to wich port wich divice connected is. 
  - To set the baudrate of the serial connection, you have to add the argument *--usbbaudrate* and after it the baudrate. If you want to avoid problems, just don't use this argument, because DMX has a baud of 250000 if you change it, it won't be DMX anymore. The default here is 250000 baud.

**Ohter:**
  - If you want to you can add the argument *--nogui* to disable the GUI.
  - If you want to you can also add the argument *--showReceive*. It will print a message in the console, every time it receives an Art-Net packet.
  - If you want to you can add the argument *--withoutusb*. If you use this, is won't open the serial port. You can only see the Art-Net output in the GUI. (Only makes scense if you don't use *--nugui*)
