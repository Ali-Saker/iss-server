package com.iss.phase1.client.tcp;

import com.iss.phase1.client.entity.Document;
import com.iss.phase1.client.entity.DocumentResponse;
import com.iss.phase1.client.extra.AES;
import com.iss.phase1.client.extra.RSA;
import com.iss.phase1.controller.DocumentController;
import com.iss.phase1.client.entity.DocumentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;

@Component
public class AsyncService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DocumentController documentController;

    @Async
    public void acceptConnections(ServerSocket serverSocket)  {
        try {
            Socket clientSocket = serverSocket.accept();
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

            TCPConnection tcpConnection = new TCPConnection(clientSocket, in, out);

            RSA.init();
            TCPObject tcpObject = tcpConnection.receive();
            if(tcpObject.getType() == TCPObjectType.PUBLIC_KEY) {
                tcpConnection.setClientPublicKey((PublicKey) tcpObject.getObject());
            }

            tcpConnection.send(new TCPObject(TCPObjectType.PUBLIC_KEY, RSA.getPublicKey()));

            TCPObject tcpObjectSessionKey = tcpConnection.receive();
            if(tcpObjectSessionKey.getType() == TCPObjectType.SESSION_KEY) {
                byte [] encryptedSessionKey = (byte []) tcpObjectSessionKey.getObject();
                AES.issSecretKey = new String(RSA.decrypt(encryptedSessionKey));
            }


            applicationContext.getBean(AsyncService.class).acceptRequests(tcpConnection);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Async
    public void send(TCPConnection connection, TCPObject data) throws IOException {
        connection.send(data);
    }

    @Async
    public void acceptRequests(TCPConnection connection) throws IOException, ClassNotFoundException {
        while (true) {
            TCPObject data = connection.receive();
            if(data.getType() != TCPObjectType.DOCUMENT) {
                continue;
            }
            Document document;
            DocumentRequest documentRequest = (DocumentRequest) data.getObject();
            documentRequest.verifyName(connection.getClientPublicKey());

            switch (documentRequest.getActionType()) {
                case FETCH:
                    document = documentController.fetch((DocumentRequest) data.getObject());
                    applicationContext.getBean(AsyncService.class).send(connection, new TCPObject(TCPObjectType.DOCUMENT,
                            new DocumentResponse(document.getName(), document.getContent()).signName().signContent()));
                    break;
                case EDIT:
                    documentRequest.verifyContent(connection.getClientPublicKey());
                    document = documentController.update((DocumentRequest) data.getObject());
                    applicationContext.getBean(AsyncService.class).send(connection, new TCPObject(TCPObjectType.DOCUMENT,
                            new DocumentResponse(document.getName(), document.getContent()).signName().signContent()));
                    break;
            }
        }
    }

}
