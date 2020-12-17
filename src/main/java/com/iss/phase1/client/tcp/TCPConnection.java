package com.iss.phase1.client.tcp;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

public class TCPConnection {

    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream out;
    private X509Certificate serverCertificate;
    private X509Certificate clientCertificate;


    public TCPConnection(Socket clientSocket, ObjectInputStream input, ObjectOutputStream out, X509Certificate serverCertificate) {
        this.clientSocket = clientSocket;
        this.input = input;
        this.out = out;
        this.serverCertificate = serverCertificate;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ObjectInputStream getInput() {
        return input;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public X509Certificate getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(X509Certificate serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public X509Certificate getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(X509Certificate clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public PublicKey getClientPublicKey() {
        return clientCertificate.getPublicKey();
    }


    public void send(TCPObject data) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    public TCPObject receive() throws IOException, ClassNotFoundException {
        return (TCPObject) input.readObject();
    }

    public boolean hasReadPermission(String docName) {
        String readSection = this.clientCertificate.getSubjectDN().getName().split(";")[1];
        return readSection.contains(docName);
    }

    public boolean hasEditPermission(String docName) {
        String editSection = this.clientCertificate.getSubjectDN().getName().split(";")[2];
        return editSection.contains(docName);
    }
}
