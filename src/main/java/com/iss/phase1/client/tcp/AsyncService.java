package com.iss.phase1.client.tcp;

import com.iss.phase1.client.entity.Document;
import com.iss.phase1.client.entity.DocumentResponse;
import com.iss.phase1.client.extra.CertificateAuthority;
import com.iss.phase1.client.extra.DigitalSignature;
import com.iss.phase1.controller.DocumentController;
import com.iss.phase1.client.entity.DocumentRequest;
import org.bouncycastle.jcajce.provider.asymmetric.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.X509Certificate;

@Component
public class AsyncService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DocumentController documentController;

    @Async
    public void acceptConnections(ServerSocket serverSocket)  {
        try {
            do {
                X509Certificate serverCertificate = CertificateAuthority.generateV1Certificate(DigitalSignature.getPublicKey(), "ISSSERVER");

                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                TCPConnection tcpConnection = new TCPConnection(clientSocket, in, out, serverCertificate);

                TCPObject tcpObject = tcpConnection.receive();
                if (tcpObject.getType() == TCPObjectType.CLIENT_CERTIFICATE) {
                    X509Certificate clientCertificate = (X509Certificate) tcpObject.getObject();
                    CertificateAuthority.verifyCertificate(clientCertificate);
                    tcpConnection.setClientCertificate(clientCertificate);
                }

                tcpConnection.send(new TCPObject(TCPObjectType.SERVER_CERTIFICATE, serverCertificate));


                applicationContext.getBean(AsyncService.class).acceptRequests(tcpConnection);
            } while (true);
        } catch (IOException | ClassNotFoundException | SignatureException | InvalidKeyException ex) {
            ex.printStackTrace();
        }
    }

    @Async
    public void send(TCPConnection connection, TCPObject data) throws IOException {
        connection.send(data);
    }

    @Async
    public void acceptRequests(TCPConnection connection) throws IOException, ClassNotFoundException, SignatureException, InvalidKeyException {
        while (true) {
            TCPObject data = connection.receive();
            if(data.getType() != TCPObjectType.DOCUMENT) {
                continue;
            }
            Document document;
            DocumentRequest documentRequest = (DocumentRequest) data.getObject();
            documentRequest.verifyName(connection.getClientPublicKey());

            if(!connection.hasReadPermission(documentRequest.getDocumentName())) {
                applicationContext.getBean(AsyncService.class).send(connection, new TCPObject(TCPObjectType.UNAUTHORIZED_READ,
                        new DocumentResponse("UNAUTHORIZED", "UNAUTHORIZED").signName().signContent()));
                continue;
            }


            switch (documentRequest.getActionType()) {
                case FETCH:
                    document = documentController.fetch((DocumentRequest) data.getObject());
                    applicationContext.getBean(AsyncService.class).send(connection, new TCPObject(
                            connection.hasEditPermission(documentRequest.getDocumentName())? TCPObjectType.DOCUMENT : TCPObjectType.UNAUTHORIZED_EDIT,
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
