package kr.co.syrup.adreport.framework.utils;

import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class StringCompression {

    public static String compressAndEncode(String input) throws Exception {
        byte[] inputBytes = input.getBytes("UTF-8");

        // Compression
        Deflater deflater = new Deflater();
        deflater.setInput(inputBytes);
        deflater.finish();

        byte[] compressedBytes = new byte[1000];  // Adjust the size accordingly
        int compressedSize = deflater.deflate(compressedBytes);

        // Encoding
        byte[] encodedBytes = Base64.getEncoder().encode(compressedBytes);

        return new String(encodedBytes, "UTF-8");
    }

    public static String decodeAndDecompress(String encodedInput) throws Exception {
        byte[] encodedBytes = encodedInput.getBytes("UTF-8");

        // Decoding
        byte[] compressedBytes = Base64.getDecoder().decode(encodedBytes);

        // Decompression
        Inflater inflater = new Inflater();
        inflater.setInput(compressedBytes, 0, compressedBytes.length);

        byte[] decompressedBytes = new byte[1000];  // Adjust the size accordingly
        int decompressedSize = inflater.inflate(decompressedBytes);

        inflater.end();

        return new String(decompressedBytes, 0, decompressedSize, "UTF-8");
    }

    public static String base64Encode(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes());
    }

    public static String base64Decode(String str) {
        byte[] decodedBytes = Base64.getDecoder().decode(str);
        String decodedStr = new String(decodedBytes);
        return decodedStr;
    }

    public static void main(String[] args) throws Exception {
        // Original long string
        String originalString = "This is a long string that we want to compress and encode.";

        // Compress and encode
        String compressedAndEncoded = compressAndEncode(originalString);
        System.out.println("Compressed and Encoded: " + compressedAndEncoded);

        // Decode and decompress
        String decodedAndDecompressed = decodeAndDecompress("eJzNVt9r21YU/leEntNYv2X7zXbS1WnseLFDKWOMK+lKuUTSNVdXTt1QCHQtIQ2sHS3LumZs0NEN+hDMHryH/kOW/D/sXklu7NVJU/IyMNic8/mc4+8755MPRDiAIa2DCDZDF4vVgzzQQ9SHYlVMj99OHx8moyfsgzD9+a/J+CgZvZ58eCykZ38no7fT4/F3k/M3yfkbYfrkhKOPx7fSp8+Tk8P03WHy7ij9fpy+fimuiAEge5Ci0Gs6rK6hVBSJRW0cUgJs2qWAxhFLSBqL5iMM+3yC2vYswDCErgHKoxsgFBRjRVAkRRNkpSpJ7CXUWjPseugUyBYYCtpSICDrHMrnMdVy8cVN7CEb+EXz+nZz7at1hvWxDSjCYRdS/hvuh2LVBX4EeZUapTB0Gjh0EIfUfJ+nKYmXZbt9aCPgbxb1rip0B8fEH16FaGAHLuTrXjMAHp98l9J+VC2VIuwAggJvNRqSuL9q49U9UkJR6aMcJc6MJJdk85728KFX39gwHNc1VNcEwJZcUJahIVuGrrtSRXYMQ4Or/dDL6OvuofCm/daifVdybEmTYMXQTctxK1DRoS2XbUOxpLJmaUU/J+77TBoK76EwZIUKie4vSW2iABX7I32abeA4pGJVXhH380AHRNE+Jg6nUmyzeoidAgkygToED1DEZfUIhAHfS/iAFmtQtF8G32Yy9xHkjcTu3enLk+TX0+TZL5egmaIR8kJM/nNyl8A7MenjCO5EnIDJ6MP0xyOBnWD69Hf29oOQvB+nZ6fsQl+wM7xY9HpMKQ7zdVezbckCdUaIj0kt4hPM9r7WbTbEC8znEZyV66GymxbS83H60/vpq9PJ+JwDFhe75SxcxmKyB4gH6Vy+kLELAbF3i86tNS5kFES1mO4yoj5qm9vDA8ZfTGAB3uqs8xS2rQ5GzGjAYJZpb7Xz8wcOt67AK+o8KgrdoYGfG+c3B3MRxrGsqUYByhxPkiSjIi+xnV32hRldO73eFp9kFutikm8qDxQU5tDGVvt2c7slzqeuUnKuQK5B8uyfdPRnZuO//cGs/VV6Nl7AXbfW5TgHDpANZ1Z3G4XOnH1eiFGnYRsEmYmgiGIyZAH+aCCQ3ayzzPDVqqZXZV3otJgQn/Cu6V/Ke7NVy1x+kXYlD2QOt0P8a5icRUDoLBrcnjZQdEuv6AYo6wZUTAu4sqkYmlRWDVORrYqszwzus3S5zII6hG1ogz82sb8kETMGA0gKj5NuQuMXr+9yGtWb0Wjajhfc/VqRNLVsVyxHZnQC3dVVyzRUCeqK69qmYbrG/4HFb1l/5LpdSPgQxZ+QHvuZw6LUrUr50b8/Zg==");
        System.out.println("Decoded and Decompressed: " + decodedAndDecompressed);
    }
}
