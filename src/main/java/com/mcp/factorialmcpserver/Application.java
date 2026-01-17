package com.mcp.factorialmcpserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.lang.NonNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
public class Application {

	static void main(String[] args) {
		System.setIn(new EofDetectingInputStream(System.in));
		/*
		Intercepts the EOF signal from the MCP client to handle connection closure.
		Required because the JDK Oauth2 Http Server's non-daemon dispatcher thread prevents automatic JVM shutdown.
		Explicitly calls System.exit(0) to ensure the Docker container stops when the MCP client disconnects.
		 */

		SpringApplication.run(Application.class, args);
	}

	private static class EofDetectingInputStream extends FilterInputStream {
		protected EofDetectingInputStream(InputStream in) {
			super(in);
		}

		@Override
		public int read() throws IOException {
			int result = super.read();
			if (result == -1) {
				System.exit(0);
			}
			return result;
		}

		@Override
		public int read(@NonNull byte[] bytes, int off, int len) throws IOException {
			int result = super.read(bytes, off, len);
			if (result == -1) {
				System.exit(0);
			}
			return result;
		}
	}

}
