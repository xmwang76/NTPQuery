package common.time;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ntp.TimeStamp;

public class NtpQueryClient {
    private static final byte[] REQUEST_PAYLOAD = "reftime,offset".getBytes();
    private static final String LOCALHOST_NAME = "localhost";
    private static final int DEFAULT_NTP_PORT = 123;
    private static final int MAX_RECEIVE_SIZE = 512;
    private static final Pattern PATTERN = Pattern.compile("reftime=0x(.*), offset=(.*)");

    private final DatagramSocket socket;

    public NtpQueryClient() {
        try {
            socket = new DatagramSocket();
        }
        catch (SocketException e) {
            throw new RuntimeException("Failed to create socket", e);
        }
    }

    public NtpQueryResult query() {
        NtpQueryMessage receiveMessage;
        try {
            NtpQueryMessage sendMessage = NtpQueryMessage.createRequestMessage(REQUEST_PAYLOAD, REQUEST_PAYLOAD.length);
            DatagramPacket sendPacket = new DatagramPacket(sendMessage.getBytes(), sendMessage.getSize(),
                InetAddress.getByName(LOCALHOST_NAME), DEFAULT_NTP_PORT);
            socket.send(sendPacket);

            DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_RECEIVE_SIZE], MAX_RECEIVE_SIZE);
            socket.receive(receivePacket);

            receiveMessage = NtpQueryMessage.createFromResponse(receivePacket.getData(), receivePacket.getLength());
            if (receiveMessage.getError() != 0) {
                throw new RuntimeException("NTP daemon returned error: " + new String(receiveMessage.getPayload()));
            }

        }
        catch (IOException e) {
            throw new RuntimeException("Failed to query NTP daemon", e);
        }
        finally {
            socket.close();
        }

        String response = new String(receiveMessage.getPayload());
        Matcher matcher = PATTERN.matcher(response);
        if (matcher.find() && matcher.groupCount() == 2) {
            long reftimeMillis = TimeStamp.parseNtpString(matcher.group(1)).getTime();
            long offsetMillis = (long) Double.parseDouble(matcher.group(2));
            return new NtpQueryResult(offsetMillis, reftimeMillis);
        }

        throw new RuntimeException("Failed to find fields reftime/offset from NTP response: " + response);
    }

    public static void main(String[] args) throws IOException {
        NtpQueryClient client = new NtpQueryClient();
        NtpQueryResult result = client.query();
        System.out.println(String.format("offset=%d, reftime=%d", result.offsetMillis, result.refTimeMillis));
    }
}
