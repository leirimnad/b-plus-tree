package io;

import tree.BPlusTree;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    static final List<String> acceptedCommands = List.of("P", "I", "D");

    public static void runFile(String fileName, BPlusTree<Integer, String> tree){
        try {
            List<Command> commands = getCommands(fileName);
            for (Command c :
                    commands) {
                c.run(tree);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

    }

    public static List<Command> getCommands(String fileName) throws IOException {
        Path filePath = Path.of(fileName);
        String file = Files.readString(filePath);
        List<Command> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile("^(["+String.join("", acceptedCommands)+"])(( -?\\d+)( .+)?)?$", Pattern.MULTILINE)
                .matcher(file);
        while (m.find()) {
            UncheckedCommand uncheckedCommand = new UncheckedCommand(m.group(1), m.group(3), m.group(4));
            if (uncheckedCommand.isCorrect())
                allMatches.add(uncheckedCommand.asCommand());
        }
        return allMatches;
    }

    private static class UncheckedCommand {
        private final String commandSymbol;
        private final Optional<Integer> key;
        private final Optional<String> value;


        public UncheckedCommand(String commandSymbolGroup, String keyGroup, String valueGroup) {
            this.commandSymbol = commandSymbolGroup;

            if (keyGroup != null)
                this.key = Optional.of(Integer.parseInt(keyGroup.substring(1)));
            else this.key = Optional.empty();

            if (valueGroup != null)
                this.value = Optional.of(valueGroup.substring(1));
            else this.value = Optional.empty();

        }

        public boolean isCorrect(){
            if (!acceptedCommands.contains(this.commandSymbol)) return false;
            if (Objects.equals(commandSymbol, "P") && (key.isPresent() || value.isPresent()))
                return false;
            if (Objects.equals(commandSymbol, "I") && key.isEmpty())
                return false;
            if (Objects.equals(commandSymbol, "D") && (key.isEmpty() || value.isPresent()))
                return false;
            return true;
        }

        public Command asCommand(){
            return new Command(this.commandSymbol, this.key, this.value);
        }
    }

    public static class Command{
        private String commandSymbol;
        private Optional<Integer> key;
        private Optional<String> value;

        public Command(String commandSymbol, Optional<Integer> key, Optional<String> value) {
            this.commandSymbol = commandSymbol;
            this.key = key;
            this.value = value;
        }

        public void run(BPlusTree<Integer, String> tree){
            if (commandSymbol.equals("P"))
                tree.print();
            if (commandSymbol.equals("I"))
                tree.insert(key.get(), value.orElse("no value"));
            if (commandSymbol.equals("D"))
                tree.delete(key.get());
        }
    }
}
