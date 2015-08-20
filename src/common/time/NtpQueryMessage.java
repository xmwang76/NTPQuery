package common.time;

import java.util.Arrays;

/**
 * NTP control message format is fully documented in RFC-1305 {@link https://tools.ietf.org/html/draft-ietf-ntp-ntpv4-proto-02#page-28}
 * <p>
 * <code>
     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |00 | VN  |  6  | REM |    Op   |           Sequence            |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |            Status             |         Association ID        |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |            Offset             |             Count             |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    .                                                               .
    .                     Data (468 Octets Max)                     .
    .                                                               .
    |                               |         Padding (zeros)       |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |                   Authenticator (optional)(96)                |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * </code>
 * <p>
 * 
 */
public class NtpQueryMessage {

    private static final int CTL_OP_READVAR = 2;

    private static final int VERSION_FOUR = 4;

    private static final int MODE_CONTROL = 6;

    private static final int HEAD_SIZE = 12;

    private static final int MODE_INDEX = 0;
    private static final int MODE_SHIFT = 0;
    private static final int MODE_SIZE = 3;

    private static final int VERSION_INDEX = 0;
    private static final int VERSION_SHIFT = 3;
    private static final int VERSION_SIZE = 3;

    private static final int OPCODE_INDEX = 1;
    private static final int OPCODE_SHIFT = 0;
    private static final int OPCODE_SIZE = 5;

    private static final int ERROR_INDEX = 1;
    private static final int ERROR_SHIFT = 5;
    private static final int ERROR_SIZE = 1;

    private static final int RESPONSE_INDEX = 1;
    private static final int RESPONSE_SHIFT = 6;
    private static final int RESPONSE_SIZE = 1;

    private static final int MORE_INDEX = 1;
    private static final int MORE_SHIFT = 7;
    private static final int MORE_SIZE = 1;

    private static final int SEQUENCE_INDEX = 2;

    private static final int STATUS_INDEX = 4;

    private static final int ASSOCIATE_ID_INDEX = 6;

    private static final int OFFSET_INDEX = 8;

    private static final int COUNT_INDEX = 10;

    private byte[] buf;

    public NtpQueryMessage(byte[] buf) {
        this.buf = buf;
    }

    public static NtpQueryMessage createRequestMessage(byte[] payload, int size) {
        byte[] buf = new byte[HEAD_SIZE + size + 2];
        System.arraycopy(payload, 0, buf, HEAD_SIZE, size);
        NtpQueryMessage m = new NtpQueryMessage(buf);
        m.setMode(MODE_CONTROL);
        m.setVersion(VERSION_FOUR);
        m.setOpCode(CTL_OP_READVAR);
        m.setSequence(0);
        m.setCount(size);
        return m;
    }

    public static NtpQueryMessage createFromResponse(byte[] response, int size) {
        return new NtpQueryMessage(Arrays.copyOf(response, size));
    }

    /***
     * Returns mode as defined in RFC-1305 which is a 3-bit integer whose value
     * is indicated by the MODE_xxx parameters.
     * 
     * @return mode as defined in RFC-1305.
     */
    public int getMode() {
        return getByteField(MODE_INDEX, MODE_SHIFT, MODE_SIZE);
    }

    /***
     * Set mode as defined in RFC-1305.
     * 
     * @param mode
     */
    public void setMode(int mode) {
        setByteField(MODE_INDEX, MODE_SHIFT, MODE_SIZE, mode);
    }

    /***
     * Returns NTP version number as defined in RFC-1305 which is a 3-bit code.
     * 
     * @return NTP version number as defined in RFC-1305.
     */
    public int getVersion() {
        return getByteField(VERSION_INDEX, VERSION_SHIFT, VERSION_SIZE);
    }

    /***
     * Set NTP version as defined in RFC-1305.
     * 
     * @param version
     *            NTP version.
     */
    public void setVersion(int version) {
        setByteField(VERSION_INDEX, VERSION_SHIFT, VERSION_SIZE, version);
    }

    /***
     * Return NTP op code as defined in RFC-1305 which is a 5-bit code.
     * 
     * @return NTP op code as defined in RFC-1305.
     * 
     */
    public int getOpCode() {
        return getByteField(OPCODE_INDEX, OPCODE_SHIFT, OPCODE_SIZE);
    }

    /***
     * Set NTP op code as defined in RFC-1305.
     * 
     * @param opCode
     *            NTP op code.
     */
    public void setOpCode(int opCode) {
        setByteField(OPCODE_INDEX, OPCODE_SHIFT, OPCODE_SIZE, opCode);
    }

    public int getError() {
        return getByteField(ERROR_INDEX, ERROR_SHIFT, ERROR_SIZE);
    }

    public void setError(int error) {
        setByteField(ERROR_INDEX, ERROR_SHIFT, ERROR_SIZE, error);
    }

    public int getResponse() {
        return getByteField(RESPONSE_INDEX, RESPONSE_SHIFT, RESPONSE_SIZE);
    }

    public void setResponse(int response) {
        setByteField(RESPONSE_INDEX, RESPONSE_SHIFT, RESPONSE_SIZE, response);
    }

    public int getMore() {
        return getByteField(MORE_INDEX, MORE_SHIFT, MORE_SIZE);
    }

    public void setMore(int more) {
        setByteField(MORE_INDEX, MORE_SHIFT, MORE_SIZE, more);
    }

    public int getByteField(int index, int shift, int size) {
        return (ui(buf[index]) >> shift) & (0xFF >> (8 - size));
    }

    public void setByteField(int index, int shift, int size, int value) {
        byte x = (byte) (0xFF >> (8 - size));
        byte y = (byte) (~(x << shift));
        buf[index] = (byte) ((buf[index] & y) | ((value & x) << shift));
    }

    public int getSequence() {
        return getShortField(SEQUENCE_INDEX);
    }

    public void setSequence(int sequence) {
        setShortField(SEQUENCE_INDEX, sequence);
    }

    public int getStatus() {
        return getShortField(STATUS_INDEX);
    }

    public void setStatus(int status) {
        setShortField(STATUS_INDEX, status);
    }

    public int getAssociateId() {
        return getShortField(ASSOCIATE_ID_INDEX);
    }

    public void setAssociateId(int associateId) {
        setShortField(ASSOCIATE_ID_INDEX, associateId);
    }

    public int getOffset() {
        return getShortField(OFFSET_INDEX);
    }

    public void setOffset(int offset) {
        setShortField(OFFSET_INDEX, offset);
    }

    public int getCount() {
        return getShortField(COUNT_INDEX);
    }

    public void setCount(int count) {
        setShortField(COUNT_INDEX, count);
    }

    /***
     * Return 2 bytes from the specified index as 16-bit short
     * 
     * @param index
     *            location of the bytes
     * @return 2 bytes as 16-bit short
     */
    private int getShortField(int index) {
        return ui(buf[index]) << 8 | ui(buf[index + 1]);
    }

    /***
     * Set the 16-bit short value at the specified index
     * 
     * @param index
     *            location of the bytes
     * @param value
     *            new value
     */
    private void setShortField(int index, int value) {
        buf[index] = (byte) (value & 0xF0);
        buf[index + 1] = (byte) (value & 0x0F);
    }

    /***
     * Convert byte to unsigned integer. Java only has signed types so we have
     * to do more work to get unsigned ops.
     * 
     * @param b
     * @return unsigned int value of byte
     */
    protected final static int ui(byte b) {
        return b & 0xFF;
    }

    /***
     * Convert byte to unsigned long. Java only has signed types so we have to
     * do more work to get unsigned ops
     * 
     * @param b
     * @return unsigned long value of byte
     */
    protected final static long ul(byte b) {
        return b & 0xFF;
    }

    /***
     * Return the reference to the full message buffer
     * 
     * @return full message buffer
     */
    public byte[] getBytes() {
        return buf;
    }

    /***
     * Return the size of message buffer
     * 
     * @return the size of full message buffer
     */
    public int getSize() {
        return buf.length;
    }

    /***
     * Return a copy of the message payload
     * 
     * @return message payload that is held beyond head
     */
    public byte[] getPayload() {
        int payloadSize = buf.length - HEAD_SIZE;
        byte[] payload = new byte[payloadSize];
        System.arraycopy(buf, HEAD_SIZE, payload, 0, payloadSize);
        return payload;
    }
}
