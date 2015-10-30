package datacomm;

public enum MessageType {
	NEW_GAME((byte)0), MOVE((byte)1), TIE((byte)2), USER_WIN((byte)3), SERVER_WIN((byte)4), END_GAME((byte)8), END_SESSION((byte)9), NO_SUCH_ENUM((byte)100);
	
	private byte code;
	
	MessageType(byte code){
		this.code = code;
	}
	
	public byte getCode() { 
		return code; 
	}
	
	public static MessageType fromValue(byte value){
		switch(value){
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
