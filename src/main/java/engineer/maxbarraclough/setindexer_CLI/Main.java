/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer_CLI;

import engineer.maxbarraclough.setindexer.Encoder;
import engineer.maxbarraclough.setindexer.Decoder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
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

    public static void main(final String[] args) throws ParseException, IOException {

        boolean exitWithError = false;

        final Options options = Main.generateOptions();

        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;

        try {
            final CommandLine cl = Main.parse(args, options);

            if (cl.hasOption('h')) { // -h can be used with or without specifying the otherwise mandatory options
                final HelpFormatter hf = new HelpFormatter();
                hf.printHelp("SetIndexer", options);
            } else {

                final boolean eOptionSet = cl.hasOption('e');
                final boolean dOptionSet = cl.hasOption('d');

                if (eOptionSet == dOptionSet) {
                    exitWithError = true;
                    System.err.println("Either the \"-e\" option or else the \"-d\" option must be specified");
                } else {
                    // // // TODO does it throw if the user fails to specify mandatory args, etc?????
                    // // // TODO do we handle multiple appearances of flags? existence of invalid flags?

                    // It returns Object, but String#toString() is the identity function.
                    // In case of unexpected trouble, throws ParseException.
                    // If arg not found, returns null.
                    final Object inputArg_Obj = cl.getParsedOptionValue("i");
                    final String inputArg_Str = inputArg_Obj.toString(); // if option missing, exception already thrown

                    InputStream inputStream = null;

                    if ("-".equals(inputArg_Str)) {
                        inputStream = System.in;
                    } else {
                        try {
                            // Avoid this. https://dzone.com/articles/fileinputstream-fileoutputstream-considered-harmful
                            // inputStream = new FileInputStream(inputArg_Str);
                            inputStream = Files.newInputStream(Paths.get(inputArg_Str));

                            // final String fullPath = new File(inputArg_Str).getAbsolutePath(); // TODO DEBUG ONLY
                            // final int dummy = 0; // DEBUG ONLY
                        } catch (final FileNotFoundException exc) { // slightly misleading name, see
                            // https://docs.oracle.com/javase/7/docs/api/java/io/FileInputStream.html#FileInputStream(java.lang.String)
                            throw new OpenInputFileException();
                        }
                    }

                    inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                    // https://stackoverflow.com/a/9938559/

                    final Object ouputArg_Obj = cl.getParsedOptionValue("o");
                    final String outputArg_Str = ouputArg_Obj.toString();

                    OutputStream outputStream = null;

                    if ("-".equals(outputArg_Str)) {
                        outputStream = System.out;
                    } else {
                        try {
                            // Fail if file already exists
                            // TODO add a "--overwrite" CLI flag to enable overwrite
                            outputStream = Files.newOutputStream(
                                    Paths.get(outputArg_Str),
                                    StandardOpenOption.WRITE,
                                    StandardOpenOption.CREATE_NEW
                            );
                        } catch (final FileNotFoundException exc) {
                            throw new OpenOutputFileException();
                        }
                    }

                    if (eOptionSet) {

                        final List<BigInteger> diffs = Encoder.encodeToDiffs(inputStreamReader);

                        inputStreamReader.close(); // No longer used. If it's stdin, that's still ok.
                        inputStreamReader = null;
                        inputStream = null;

                        outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
                        Encoder.encodeAndPrint_NumericalOutput(outputStreamWriter, diffs);
                    } else { // decode
                        System.err.println("[Decode functionality not yet implemented]");

                        Decoder.decode_NumericalOutput(inputStreamReader);
                        // // // // TODO sort out output stream/ret

                        inputStreamReader.close();
                        inputStreamReader = null;
                        inputStream = null;
                    }
                }
            }
        } catch (final OpenInputFileException exc) {
            exitWithError = true;
            System.err.println("Unable to open the specified input file. Exiting.");
        } catch (final OpenOutputFileException exc) {
            exitWithError = true;
            System.err.println("Unable to open the specified output file. Exiting.");
        } catch (final ParseException exc) {
// -h can be used with or without specifying the otherwise mandatory options. The latter case means ParseException
// This code doesn't care whether the "-h"/"--help" was intended as an argument. That's fine.
            boolean printHelp = false;
            for (int i = 0; i != args.length; i = Math.addExact(i, 1)) // addExact is, technically, extra correct
            { // order equals call to throw if args[i] is somehow null:
                if (args[i].startsWith("-h") || args[i].equals("--help")) {
                    printHelp = true;
                    break;
                }
            }
            if (printHelp) {
                final HelpFormatter hf = new HelpFormatter();
                hf.printHelp("SetIndexer", options);
            } else {
                exitWithError = true;
                System.err.println("Invalid command-line options.");
                System.err.println(exc.getMessage());
                System.err.println("Exiting.");
            }
        } catch (final Exception exc) {
            exitWithError = true;
            System.err.println("An unexpected error occurred. Exiting.");
        } finally {
            // Even if one of these tidy-up operations fails, go ahead with the other before throwing
            try {
                if (null != inputStreamReader) { // it's fine to possibly close stdin
                    inputStreamReader.close();
                    inputStreamReader = null;
                }
            } finally {
                if (null != outputStreamWriter) { // it's fine to possibly close stdout
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    outputStreamWriter = null;
                }
            }
        }

        if (exitWithError) {
            System.exit(1);
        }
    }



    /**
     * Intended to be called exactly once
     *
     * @return
     */
    private static final Options generateOptions() {
        final Options options = new Options();

        // TODO use globals and avoid 'magic string literals'
        options.addOption("h", "help", false, "Print this message");
        options.addOption("e", "encode", false, "Encode a file of lines, the order of which isn't significant, into a blob");
        options.addOption("d", "decode", false, "Decode a blob back into a file of lines, the order of which might not be preserved");
        options.addRequiredOption("i", "input", true, "Input file path, or '-' to read standard input");
        options.addRequiredOption("o", "output", true, "Output file path, or '-' to write to standard output");

        return options;
    }

    private static final CommandLine parse(final String[] args, final Options options) throws ParseException {
        final CommandLineParser parser = new DefaultParser();
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
