/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer_CLI;

import org.apache.commons.cli.*;

/**
 *
 * @author mb
 */
public final class Main {


    public static void main(final String[] args) {

    }


    private final CommandLine parse(final String[] args) throws ParseException
    {
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        options.addRequiredOption("e", "encode", true, "Encode a file of lines, the order of which isn't significant, into a blob");
        options.addRequiredOption("d", "decode", true, "Decode a blob back into a file of lines, the order of which might not be preserved");
        options.addRequiredOption("i", "input", true, "Input file path, or '-' to read standard input");
        options.addRequiredOption("o", "output", true, "Output file path, or '-' to write to standard output");

        final CommandLine ret = parser.parse(options, args);
        return ret;
    }

    public static void EXAMPLEmain(final String[] args) {
        // Parsing code based on https://commons.apache.org/proper/commons-cli/usage.html
// create the command line parser
        final CommandLineParser parser = new DefaultParser();

// create the Options
        final Options options = new Options();
        options.addOption("a", "all", false, "do not hide entries starting with .");
        options.addOption("A", "almost-all", false, "do not list implied . and ..");
        options.addOption("b", "escape", false, "print octal escapes for nongraphic "
                + "characters");
        options.addOption(OptionBuilder.withLongOpt("block-size")
                .withDescription("use SIZE-byte blocks")
                .hasArg()
                .withArgName("SIZE")
                .create());
        options.addOption("B", "ignore-backups", false, "do not list implied entried "
                + "ending with ~");
        options.addOption("c", false, "with -lt: sort by, and show, ctime (time of last "
                + "modification of file status information) with "
                + "-l:show ctime and sort by name otherwise: sort "
                + "by ctime");
        options.addOption("C", false, "list entries by columns");

        // String[] args = new String[]{"--block-size=10"};

        try {
            // parse the command line arguments
            final CommandLine line = parser.parse(options, args);

            // validate that block-size has been set
            if (line.hasOption("block-size")) {
                // print the value of block-size
                System.out.println(line.getOptionValue("block-size"));
            }
        } catch (ParseException exp) {
            System.out.println("Unexpected exception:" + exp.getMessage());
        }
    }
}
