import java.io.InputStream;

public interface SenderBoth {
	void send(String host, int port, InputStream inputStream);
}
