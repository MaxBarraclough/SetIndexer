/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 *
 * @author mb
 */
public final class Decoder {

    private Decoder(){}


    public static final void decode_PrintNumerical( // TODO rename to decode_Numerical
            final InputStreamReader isr,
            final OutputStreamWriter osw
    )
            throws IOException
    {
        assert(null != isr);
        assert(null != osw);

        final BufferedReader br = new BufferedReader(isr);

        // final ArrayList<BigInteger> bigIntegers = new ArrayList<>();
        // No need for this

        final BufferedWriter bw = new BufferedWriter(osw);
        final PrintWriter pw = new PrintWriter(bw);

        BigInteger acc = BigInteger.ZERO; // TODO micro-optimise away the first 'add' operation?

        for (String line = br.readLine(); line != null; line = br.readLine())
        {
            // Careful that our character-encoding handling, and 1-bit-append,
            // work to match the Encode class.
            // Remember that the task of mapping each BigInteger back to a string,
            // is 'internal' to that BigInteger. The accumulator doesn't care
            // that we happen to be using BigInteger to represent String instances.

            final BigInteger uncorrected = new BigInteger(line);
            // still has the special 1 byte in [0]

            acc = acc.add(uncorrected);

            final String currentDecodedString = mapToString(acc);
            pw.println(currentDecodedString);
        }
        pw.flush(); // don't need to close()
    }


    public static final void decode_Serialization(
            final java.io.InputStream inputStream,
            final OutputStreamWriter osw
    )
            throws IOException, ClassNotFoundException {

        // following https://www.tutorialspoint.com/java/java_serialization.htm
        // // TODO buffering
        final java.io.ObjectInputStream ois
                = new java.io.ObjectInputStream(inputStream);

        final Object diffsObj = ois.readObject(); // call exactly once

        final List<BigInteger> diffs = (List<BigInteger>)diffsObj;

        final BufferedWriter bw = new BufferedWriter(osw); // TODO move to call site
        final PrintWriter pw = new PrintWriter(bw);

        BigInteger acc = BigInteger.ZERO; // TODO micro-optimise away the first 'add' operation?

        for (final BigInteger uncorrected : diffs)
        {
            acc = acc.add(uncorrected);

            final String currentDecodedString = mapToString(acc);
            pw.println(currentDecodedString);
        }
        pw.flush(); // don't need to close() anything
    }




    /**
     * Maps an uncorrected BigInteger (with its '1' marker byte in [0])
     * to the corresponding UTF-8 string
     * @param bi
     * @return
     */
    private static String mapToString(final BigInteger bi)
    {
        assert (!bi.equals(BigInteger.ZERO)); // no marker byte -> invalid input
        assert (bi.compareTo(BigInteger.ZERO) > 0); // marker byte should make it positive

        final byte[] allBytes = bi.toByteArray();
        final int allBytes_length = allBytes.length;
        final int allBytes_lengthMinus1 = allBytes.length - 1;
        assert (allBytes_length >= 2);
        final byte[] chopped = new byte[allBytes_lengthMinus1];
        System.arraycopy(allBytes, 1, chopped, 0, allBytes_lengthMinus1); // Yes, ghastly!

        final String ret = new String(chopped, StandardCharsets.UTF_8);
        return ret;
    }

}
