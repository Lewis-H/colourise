package colourise.networking.protocol;

/**
 * Parses the byte messages
 */
public class Parser {
    // Command
    private Command command = null;
    // Arguments
    private byte[] arguments = null;
    // Length
    private int count = 0;

    // Get remaining arguments to be filled
    public int getRemaining() {
        return command != null ? Command.getLength(command) - getCount() : 1;
    }

    // Get arguments added
    public int getCount() {
        return count;
    }

    /**
     * Adds a byte to the parser
     * @param b Byte to add
     * @return Whether the length has been reached
     */
    public boolean add(byte b) {
        if(command == null) {
            command = Command.fromInt(b);
            arguments = new byte[Command.getLength(command)];
        }else{
            arguments[count++] = b;
        }
        return count == arguments.length;
    }

    /**
     * Adds a byte array to the parser
     * @param bytes The bytes to add
     * @return Whether the length has been reached
     */
    public boolean add(byte[] bytes) {
        boolean finished = false;
        for(byte b : bytes)
            finished = add(b);
        return finished;
    }

    /**
     * Constructs a message from the collected input
     * @return Message
     */
    public Message create() {
        return new Message(command, arguments);
    }

    /**
     * Resets the input
     */
    public void reset() {
        command = null;
        arguments = null;
        count = 0;
    }
}
