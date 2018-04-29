/*
 * Licence: see LICENCE.txt
 */
package engineer.maxbarraclough.setindexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author mb
 */
public final class Encoder {

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

    public static void encode(final InputStreamReader isr, final OutputStreamWriter osw) throws IOException
    {
        final BufferedReader br = new BufferedReader(isr);

        final ArrayList<String> al = new ArrayList<>(); // TODO optimise out this intermediate store. We can stream directly.

        for (String line = br.readLine(); line != null; line = br.readLine())
        {
            al.add(line);
        }

        al.sort(stringUnicodeComparator);

        // TODO we've converted bytes to chars (String), and here we go back again
        // before converting to BigInteger. This could be optimised away.

        final List<BigInteger> bigIntegers =
                al.stream()
                        .map((final String s) -> {
                            return new BigInteger(s.getBytes());
                        })
                        .collect(Collectors.toList());

        // Sliding window: now we loop over each window of 2 BigInteger
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

            for (int lowerIdx = 1; // start at 1, not 0
                    lowerIdx <= maxValidIdx_Minus1;
                    lowerIdx = Math.addExact(lowerIdx, 1)) {
                final int upperIdx = Math.addExact(lowerIdx, 1);

                final BigInteger lower = bigIntegers.get(lowerIdx);
                final BigInteger upper = bigIntegers.get(upperIdx);

                final BigInteger diff = upper.subtract(lower);
                diffs.add(diff);
            }
        }
        // else bigIntegers is empty
        // TODO handle that

        // Map each string to a BigInteger, then compute the diffs.
        // Ensure empty and singleton cases are handled correctly.



        // TODO somehow print out the diffs list to a compact binary representation

    }
}
