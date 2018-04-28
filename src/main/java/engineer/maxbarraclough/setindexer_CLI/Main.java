/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer_CLI;

// import java.util.Arrays;
// import java.util.Collections;
// import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.commons.cli.*;

/**
 *
 * @author mb
 */
public final class Main {

    private static final class OpenInputFileException extends Exception {
    }

    private static final class OpenOutputFileException extends Exception {
    }

    public static void main(final String[] args) throws ParseException {

        boolean exitWithError = false;

        try {

            // No good: the param is of type String[]
            // final List<String> asList = Arrays.asList(args);
            // final List<String> asUnmodList = Collections.unmodifiableList(asList);
            final CommandLine cl = Main.parse(args);

            final boolean eOptionSet = cl.hasOption('e');
            final boolean dOptionSet = cl.hasOption('d');

            if (eOptionSet == dOptionSet) {
                exitWithError = true;
                System.err.println("Either the \"-e\" option or the \"-d\" option must be specified");
                // Game over, do not continue
            } else {
                // // // TODO does it throw if the user fails to specify mandatory args, etc?????
                // // // TODO do we handle multiple appearances of flags? existence of invalid flags?

                // It returns Object, but String#toString() is the identity function.
                // In case of unexpected trouble, throws ParseException.
                // If arg not found, returns null.
                final Object inputArg_Obj = cl.getParsedOptionValue("i");
                final String inputArg_Str = (null == inputArg_Obj ? null : inputArg_Obj.toString()); // TODO is null possible here?

                InputStream inputStream = null;

                if ("-".equals(inputArg_Str)) {
                    inputStream = System.in;
                } else {
                    try {
                        inputStream = new FileInputStream(inputArg_Str);
                    } catch (final FileNotFoundException exc) { // slightly misleading name, see
                        // https://docs.oracle.com/javase/7/docs/api/java/io/FileInputStream.html#FileInputStream(java.lang.String)
                        throw new OpenInputFileException();
                    }
                }

                final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                // https://stackoverflow.com/a/9938559/

                final Object ouputArg_Obj = cl.getParsedOptionValue("i");
                final String outputArg_Str = (null == ouputArg_Obj ? null : ouputArg_Obj.toString());

                OutputStream outputStream = null;

                if ("-".equals(outputArg_Str)) {
                    outputStream = System.out;
                } else {
                    try {
                        outputStream = new FileOutputStream(outputArg_Str);
                    } catch (final FileNotFoundException exc) {
                        throw new OpenOutputFileException();
                    }
                }

                final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            }
        } catch (final OpenInputFileException exc) {
            exitWithError = true;
            System.err.println("Unable to open the specified input file. Exiting.");
        } catch (final OpenOutputFileException exc) {
            exitWithError = true;
            System.err.println("Unable to open the specified output file. Exiting.");
        } finally {
        }

        if (exitWithError) {
            System.exit(1);
        }
    }

    private static final CommandLine parse(final String[] args) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
        final Options options = new Options();

        // TODO use globals and avoid 'magic string literals'
        options.addOption("e", "encode", false, "Encode a file of lines, the order of which isn't significant, into a blob");
        options.addOption("d", "decode", false, "Decode a blob back into a file of lines, the order of which might not be preserved");
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
