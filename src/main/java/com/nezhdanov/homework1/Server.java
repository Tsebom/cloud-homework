package com.nezhdanov.homework1;

import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// TODO: 14.06.2021
	// организовать корректный вывод статуса
	// подумать почему так реализован цикл в ClientHandler
	/** Метод read() в цикле блокирует поток чтения и разблокирует его при возврате -1
	 * поэтому количество итераций цикла for должна ровнятся количеству полностью заполненых буферов +
	 * неполный буфер(если он есть) + 1 итерация где read() вернет -1
	 * для этого мы к длине фаила добавляем длину буфера и делим на длину буфера получая
	 * количество буферов помещающихся в фаил(включая неполный) + 1 буфер(возврат -1)
	 */
	public Server() {
		ExecutorService service = Executors.newFixedThreadPool(4);
		try (ServerSocket server = new ServerSocket(5678)){
			System.out.println("Server started");
			while (true) {
				service.execute(new ClientHandler(server.accept()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}
