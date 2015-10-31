package datacomm;

/**
 * Custom Enum MessageType used for simplification of message handling. Each
 * initial byte of the message represents a different possible message purpose
 * entailed below. This enum is used by server sessions and game controllers to
 * indicate types of sent and received messages.
 * 
 * @author Christopher Dufort
 * @author Elliot Wu
 * @author Nader Baydoun
 * 
 * @version Java 1.8
 */
public enum MessageType {
	NEW_GAME((byte) 0), MOVE((byte) 1), TIE((byte) 2), USER_WIN((byte) 3), SERVER_WIN((byte) 4), END_GAME(
			(byte) 8), END_SESSION((byte) 9), NO_SUCH_ENUM((byte) 100);

	private byte code;

	/**
	 * Enum constructor accepts a code and updates private field.
	 * 
	 * @param code
	 *            provided byte value of enum.
	 */
	MessageType(byte code) {
		this.code = code;
	}

	/**
	 * 
	 * @return code the associated value.
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * 
	 * @param value
	 *            byte values used within messages associated with an enum name.
	 * @return enum associated with values provided.
	 */
	public static MessageType fromValue(byte value) {
		switch (value) {
		case 0:
			return NEW_GAME;
		case 1:
			return MOVE;
		case 2:
			return TIE;
		case 3:
			return USER_WIN;
		case 4:
			return SERVER_WIN;
		case 8:
			return END_GAME;
		case 9:
			return END_SESSION;
		default:
			return NO_SUCH_ENUM;
		}
	}
}
