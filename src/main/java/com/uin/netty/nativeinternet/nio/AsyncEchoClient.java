//package com.uin.netty.nativeinternet.nio;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousSocketChannel;
//import java.nio.channels.CompletionHandler;
//import java.util.Scanner;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//
//public class AsyncEchoClient {
//  public static void main(String[] args) {
//    try (AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open()) {
//      Future<Void> connectFuture = clientChannel.connect(new InetSocketAddress("localhost", 5000));
//      connectFuture.get(); // Wait for the connection to complete
//
//      Scanner scanner = new Scanner(System.in);
//      while (true) {
//        System.out.print("Enter message: ");
//        String message = scanner.nextLine();
//
//        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
//        clientChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {
//          @Override
//          public void completed(Integer result, Void
//
