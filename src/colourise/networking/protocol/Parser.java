package colourise.networking.protocol;

public class Parser {
    private Command command = null;
    private byte[] arguments = null;
    private int count = 0;

    // Get remaining arguments to be filled
    public int getRemaining() {
        return command != null ? Command.getLength(command) - getCount() : 1;
    }

    // Get arguments added
    public int getCount() {
        return count;
    }

    public boolean add(byte b) {
        if(command == null) {
            command = Command.fromInt(b);
            arguments = new byte[Command.getLength(command)];
        }else{
            arguments[count++] = b;
        }
        return count == arguments.length;
    }

    public boolean add(byte[] bytes) {
        boolean finished = false;
        for(byte b : bytes)
            finished = add(b);
        return finished;
    }

    public Message create() {
        return new Message(command, arguments);
    }

    public void reset() {
        command = null;
        arguments = null;
        count = 0;
    }
}
