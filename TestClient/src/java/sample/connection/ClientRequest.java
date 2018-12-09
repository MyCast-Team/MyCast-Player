package sample.connection;

public enum ClientRequest {
    STREAMING_STARTED,
    DISCONNECTION,
    UNKNOWN_REQUEST;

    public static ClientRequest getValueFromData(String data) {
        if (null == data) {
            return DISCONNECTION;
        }

        try {
            int receivedData = Integer.parseInt(data);
            return values()[receivedData];
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return UNKNOWN_REQUEST;
        }
    }
}
