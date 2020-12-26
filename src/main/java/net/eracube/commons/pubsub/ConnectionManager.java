package net.eracube.commons.pubsub;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import net.eracube.Artemis;
import net.eracube.commons.packets.Packet;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {
    private final Artemis artemis;
    private Connection connection;
    private Channel channel;

    public ConnectionManager(Artemis artemis, String user, String password, String host) {
        this(artemis, user, password, host, 5672);
    }

    public ConnectionManager(Artemis artemis, String user, String password, String host, int port) {
        this.artemis = artemis;
        ConnectionFactory factory = new ConnectionFactory();
        try {
            factory.setUri("amqp://" + user + ":" + password + "@" + host + ":" + port);
        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        try {
            assert connection != null;
            channel = connection.createChannel();
            channel.exchangeDeclare("Artemis", "fanout");
            String queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, "Artemis", "");

            DeliverCallback deliverCallback = (consumerTag, delivery) ->
                    artemis.getPacketManager().receivePacket(new String(delivery.getBody(), StandardCharsets.UTF_8));

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendPacket(String tag, Packet packet) {
        try {
            int id = artemis.getPacketManager().getPacketId(packet);
            String json = artemis.getGson().toJson(packet);

            channel.exchangeDeclare("Artemis", "fanout");
            channel.basicPublish("Artemis", "", null, (tag + ";" + id + ";" + json).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            this.connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}