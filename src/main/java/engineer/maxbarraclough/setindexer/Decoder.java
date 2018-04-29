/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mb
 */
public final class Decoder {

    private Decoder(){}


    public static final void decode_NumericalOutput(final InputStreamReader isr) throws IOException
    {
        final BufferedReader br = new BufferedReader(isr);

        // final ArrayList<BigInteger> bigIntegers = new ArrayList<>();
        // No need for this

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

//            final int indexOfHighestSetBit = -1; // // //
//            final BigInteger corrected = uncorrected.clearBit(indexOfHighestSetBit);

            acc = acc.add(uncorrected);


            final String currentDecodedString = mapToString(acc);



            //////// DEV/DEBUG ONLY. SHOULD PARAMETERISE WITH AN OUTPUT STREAM, OR RETURN A COLLECTION ////////
            /////////////////////
            System.out.println(currentDecodedString);
            /////////////////////
            /////////////////////

            // bigIntegers.add(acc);
        }
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
