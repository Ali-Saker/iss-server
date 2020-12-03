package com.iss.phase1.client.tcp;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class TCPConnection {

    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream out;


    public TCPConnection(Socket clientSocket, ObjectInputStream input, ObjectOutputStream out) {
        this.clientSocket = clientSocket;
        this.input = input;
        this.out = out;
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

    public void send(TCPObject data) throws IOException {
        out.writeObject(data);
        out.flush();
    }

    public TCPObject receive() throws IOException, ClassNotFoundException {
        return (TCPObject) input.readObject();
    }
}
