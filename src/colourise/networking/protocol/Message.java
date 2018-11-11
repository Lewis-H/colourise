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
    }
}
