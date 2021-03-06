package io;

import tree.BPlusTree;
import util.RationalNumber;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser {
    static final List<String> acceptedCommands = List.of("P", "I", "DR", "D", "IR", "G");

    public static void runFile(String fileName, BPlusTree<RationalNumber, String> tree){
        try {
            List<Command> commands = getCommands(fileName);
            for (Command c :
                    commands) {
                c.run(tree);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static BPlusTree<RationalNumber, String> runFile(String fileName){
        Path filePath = Path.of(fileName);
        BPlusTree<RationalNumber, String> tree;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()));
            String firstLine = br.readLine();
            Matcher m = Pattern.compile("^BRANCHING FACTOR (\\d+)$", Pattern.MULTILINE).matcher(firstLine);
            if (!m.find())
                throw new MissingFormatArgumentException("Missing branch factor at the start of the file. " +
                        "Put \"BRANCHING FACTOR X\" at the start of the file.");
            else {
                int level = Integer.parseInt(m.group(1));
                if (level < 3)
                    throw new IllegalArgumentException("Invalid branching factor");
                tree = new BPlusTree<>(level);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        runFile(fileName, tree);
        return tree;
    }

    public static List<Command> getCommands(String fileName) throws IOException {
        Path filePath = Path.of(fileName);
        String file = Files.readString(filePath);
        List<Command> allMatches = new ArrayList<>();
        Matcher m = Pattern.compile(
                "^("+String.join("|", acceptedCommands)+")(( -?\\d+/\\d+)( .+)?)?$"
                        , Pattern.MULTILINE)
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
        private final Optional<RationalNumber> key;
        private final Optional<String> value;


        public UncheckedCommand(String commandSymbolGroup, String keyGroup, String valueGroup) {
            this.commandSymbol = commandSymbolGroup;

            if (keyGroup != null)
                this.key = Optional.of(RationalNumber.fromString(keyGroup.substring(1)));
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
            if (Objects.equals(commandSymbol, "IR") && (key.isEmpty() || !key.get().isNotNegative()))
                return false;
            if (Objects.equals(commandSymbol, "DR") && (key.isPresent() || value.isEmpty() || value.get().matches("\\d+")))
                return false;
            if (Objects.equals(commandSymbol, "G") && (key.isEmpty() || value.isPresent()))
                return false;
            return true;
        }

        public Command asCommand(){
            return new Command(this.commandSymbol, this.key, this.value);
        }
    }

    public static class Command{
        private final String commandSymbol;
        private final Optional<RationalNumber> key;
        private final Optional<String> value;

        public Command(String commandSymbol, Optional<RationalNumber> key, Optional<String> value) {
            this.commandSymbol = commandSymbol;
            this.key = key;
            this.value = value;
        }

        public void run(BPlusTree<RationalNumber, String> tree){
            if (commandSymbol.equals("P"))
                tree.print();
            if (commandSymbol.equals("I"))
                tree.insert(key.get(), value.orElse("no value"));
            if (commandSymbol.equals("D"))
                tree.delete(key.get());
            if (commandSymbol.equals("G"))
                System.out.println(tree.get(key.get()));
            if (commandSymbol.equals("IR")) {
                int needed = key.get().getNumerator();
                int max = key.get().getDenominator();

                List<RationalNumber> numbers = new ArrayList<>();
                for (int i = 0; i < max; i++)
                    numbers.add(new RationalNumber(i, 1));
                Collections.shuffle(numbers);

                int added = 0;
                while (added < needed && !numbers.isEmpty()) {
                    RationalNumber num = numbers.remove(0);
                    if (!tree.contains(num)) {
                        tree.insert(num, "R");
                        added++;
                    }
                }
            }
            if (commandSymbol.equals("DR")) {
                int needed = Integer.parseInt(value.get());
                List<RationalNumber> keys = tree.getKeys();
                Collections.shuffle(keys);
                while (needed > 0 && !keys.isEmpty()){
                    tree.delete(keys.remove(0));
                    needed--;
                }
            }

        }
    }
}
