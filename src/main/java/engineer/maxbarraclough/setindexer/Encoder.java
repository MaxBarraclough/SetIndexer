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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author mb
 */
public final class Encoder {

    private Encoder(){}

    // I don't get https://stackoverflow.com/a/11176397/ why not just wrap .compare directly?
    private static final Comparator<String> stringUnicodeComparator = new Comparator<String>() {
    public int compare(String str1, String str2) {
        int ret = str1.compareTo(str2);
        return ret;

//        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
//        if (res == 0) {
//            res = str1.compareTo(str2);
//        }
//        return res;
    }
};

    public static List<BigInteger> encodeToDiffs(final InputStreamReader isr) throws IOException
    {
        final BufferedReader br = new BufferedReader(isr);

        final ArrayList<String> al = new ArrayList<String>(); // TODO optimise out this intermediate store. We can stream directly.

        for (String line = br.readLine(); line != null; line = br.readLine())
        {
            al.add(line);
        }

        // al.sort(stringUnicodeComparator); // not the same order as BigInteger!

        // TODO we've converted bytes to chars (String), and here we go back again
        // before converting to BigInteger. This could be optimised away.

        final List<BigInteger> bigIntegers =
                al.parallelStream()
                        .map((final String s) -> {
                            // Prepend a '1' byte before the real byte sequence,
                            // because <00> is different from <0>
                            final int sLength = s.length();
                             // assume this works sensibly, given that we're using UTF-8

                             final byte[] sBytes = s.getBytes(StandardCharsets.UTF_8);

                             final byte[] bytes  = new byte[Math.addExact(sLength, 1)];
                             bytes[0] = (byte)1;
                             System.arraycopy(sBytes, 0, bytes, 1, sLength); // Yes, ghastly!

                             return new BigInteger(bytes);
                             // TODO vertical parallelisation of this step?
                        })
                        .collect(Collectors.toList());
        // TODO if we rework the IO, we could avoid a redundant copy or two

        // Only now do we do the sort
        Collections.sort(bigIntegers);

//        for(BigInteger bi : bigIntegers) {
//            System.out.println(bi);
//        }


        final int bigIntegers_size = bigIntegers.size();

        final ArrayList<BigInteger> diffs = new ArrayList<>(bigIntegers_size);
        // First elem is just copied, subsequent elements hold the
        // (necessarily positive) diff from the previous element.
        // TODO optimise this away and do it all in one ArrayList

        if (bigIntegers_size > 0) {

            diffs.add(bigIntegers.get(0));

            final int maxValidIdx = Math.subtractExact(bigIntegers_size, 1);
            final int maxValidIdx_Minus1 = Math.subtractExact(maxValidIdx, 1);
            assert(maxValidIdx_Minus1 >= 0);

            // Sliding window (width:2) across the full range, doing (n - 1) List#add operations

            for (int lowerIdx = 0; // start at 0, not 1
                    lowerIdx <= maxValidIdx_Minus1;
                    lowerIdx = Math.addExact(lowerIdx, 1)) {
                final int upperIdx = Math.addExact(lowerIdx, 1);

                final BigInteger lower = bigIntegers.get(lowerIdx);
                final BigInteger upper = bigIntegers.get(upperIdx);

                final BigInteger diff = upper.subtract(lower);
                diffs.add(diff);
            }
        }
        // else bigIntegers is empty, so leave the diffs list empty

        return diffs; // Collections.unmodifiableList(diffs);
        // TODO somehow print out the diffs list to a compact binary representation
    }


    public static final void encodeAndPrint_NumericalOutput(
            final OutputStreamWriter outputStreamWriter,
            final List<BigInteger> diffs
    )
            throws IOException {
        // This block simply dumps out in decimal/UTF-8, one line per BigInteger.
        // TODO attempt a proper compact format, perhaps using
        { // How many layers of stream indirection does Java want!!??
            final BufferedWriter bw = new BufferedWriter(outputStreamWriter);
            final PrintWriter pw = new PrintWriter(bw); // lets us do println
            for (BigInteger bi : diffs) {
                pw.println(bi.toString());
            }
            pw.flush();
            // bw.flush();
        }
    }

}
