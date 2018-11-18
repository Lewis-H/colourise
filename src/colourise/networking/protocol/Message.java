package colourise.networking.protocol;

public class Message {
    private final Command command;
    private final byte[] arguments;

    public Command getCommand() {
        return command;
    }

    public int arguments() {
        return arguments.length;
    }

    public byte getArgument(int index) {
        return arguments[index];
    }

    public Message(Command command, byte[] arguments) {
        this.command = command;
        this.arguments = arguments;
        assert Command.getLength(command) == arguments.length;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[arguments.length + 1];
        bytes[0] = command.toByte();
        for(int i = 0; i < arguments.length; i++)
            bytes[i + 1] = arguments[i];
        return bytes;
    }
}
