import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class SAW implements SenderBoth {

	private final Random r = new Random(System.currentTimeMillis());
	private final int loss;

	private final MainClass frame;

	public SAW(int loss, MainClass frame) {
		this.loss = loss;
		this.frame = frame;
	}

	@Override
	public void send(String host, int port, InputStream inputStream) {
		int v = Values.FIRST_VERSION;
		host=Values.HOST;
		int sent = 0;
		int lost = 0;
		long start = System.currentTimeMillis();
		try (DatagramSocket socket = new DatagramSocket()) {
			socket.setSoTimeout(500);
			byte[] data = new byte[Values.PACKET_SIZE];
			while (true) {
				int size = inputStream.read(data, Values.VERSION_SIZE,
						data.length - Values.VERSION_SIZE);
				if (size < 0) {
					size = 0;
					v = Values.LAST_VERSION;
					data = new byte[Values.VERSION_SIZE];
				}
				size += Values.VERSION_SIZE;

				do {
					byte[] vb = Utilities.fromVersion(v);
					System.arraycopy(vb, 0, data, 0,
							vb.length);
					sent += size;

					int randomNumber = r.nextInt(100);
					if (randomNumber < loss) {
						lost += size;
						frame.append("Lost package " + v);
					} else {
						frame.append("Sending package " + v + " (" + size + ")");
						InetAddress ip = InetAddress.getByName(host);
						DatagramPacket packet = new DatagramPacket(data,
								size, ip, port);
						socket.send(packet);
					}

					byte[] responseData = new byte[Values.VERSION_SIZE];
					DatagramPacket responsePacket = new DatagramPacket(
							responseData, responseData.length);
					try {
						socket.receive(responsePacket);
						break;
					} catch (IOException e) {
						;
					}
				} while (true);

				if (v == Values.LAST_VERSION) {
					break;
				}
				v++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		long end = System.currentTimeMillis();
		frame.append("Total Time: " + (end - start) + "ms");
		frame.append("Sent Bytes: " + sent);
		frame.append("Lost Bytes: " + lost);
	}
}
