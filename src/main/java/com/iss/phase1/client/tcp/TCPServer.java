package com.iss.phase1.client.tcp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

@Component
public class TCPServer {

    @Autowired
    private AsyncService asyncService;

    public void run(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        asyncService.acceptConnections(serverSocket);
    }
}
