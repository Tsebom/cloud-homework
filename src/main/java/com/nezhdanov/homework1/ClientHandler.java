package com.nezhdanov.homework1;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private final Socket socket;

	public ClientHandler(Socket socket) {
		this.socket = socket;
	}


	@Override
	public void run() {
		try (
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				DataInputStream in = new DataInputStream(socket.getInputStream())
		) {
			System.out.printf("Client %s connected\n", socket.getInetAddress());
			while (true) {
				String command = in.readUTF();
				if ("upload".equals(command)) {
					try {
						File file = new File("server"  + File.separator + in.readUTF());
						if (!file.exists()) {
							 file.createNewFile();
						}
						FileOutputStream fos = new FileOutputStream(file);

						long size = in.readLong();

						byte[] buffer = new byte[8 * 1024];

						for (int i = 0; i < (size + (buffer.length - 1)) / (buffer.length); i++) {
							int read = in.read(buffer);
							fos.write(buffer, 0, read);
						}
						fos.close();
						out.writeUTF("OK");
					} catch (Exception e) {
						out.writeUTF("FATAL ERROR");
					}
				}

				if ("download".equals(command)) {
					// TODO: 14.06.2021
					File file = new File("server" + File.separator + in.readUTF());
					if (file.exists()) {
						out.writeLong(file.length());
						FileInputStream fileInputStream = new FileInputStream(file);
						byte[] buff = new byte[8 * 1024];
						int read = 0;
						while ((read = fileInputStream.read(buff)) > 0) {
							out.write(buff, 0, read);
						}
						out.flush();
						fileInputStream.close();
						String status = "Downloading status is " + in.readUTF();
						out.writeUTF(status);
					}
					else {
						out.writeLong(-1);
					}
				}
				if ("exit".equals(command)) {
					System.out.printf("Client %s disconnected correctly\n", socket.getInetAddress());
					break;
				}

//				System.out.println(command);
//				out.writeUTF(command);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
