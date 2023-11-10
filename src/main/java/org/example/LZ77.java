package org.example;

import java.io.*;
import java.util.ArrayList;

public class LZ77 {
    public static class Trio {
        int offset;
        int length;
        char nextChar;

        Trio(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }
    }

    private ArrayList<Trio> compress(String input, int windowSize) {
        ArrayList<Trio> compressedData = new ArrayList<>();
        int lookAheadBufferSize = windowSize;
        int searchBufferStart = 0;

        while (searchBufferStart < input.length()) {
            int maxLength = 0;
            int offset = 0;

            // Используем for-each цикл для более ясного кода
            for (int i = Math.max(0, searchBufferStart - windowSize); i < searchBufferStart; i++) {
                int length = 0;

                // Используем Math.min, чтобы избежать выхода за пределы строки
                while (length < lookAheadBufferSize && searchBufferStart + length < input.length()
                        && input.charAt(i + length) == input.charAt(searchBufferStart + length)) {
                    length++;
                }

                if (length > maxLength) {
                    maxLength = length;
                    offset = searchBufferStart - i;
                }
            }

            char nextChar = (searchBufferStart + maxLength < input.length()) ? input.charAt(searchBufferStart + maxLength) : '\0';
            compressedData.add(new Trio(offset, maxLength, nextChar));
            searchBufferStart += (maxLength + 1);
        }

        return compressedData;
    }


    private String decompress(ArrayList<Trio> compressedData) {
        StringBuilder decompressedData = new StringBuilder();

        for (Trio trio : compressedData) {
            int startIndex = decompressedData.length() - trio.offset;
            int endIndex = startIndex + trio.length;

            for (int i = startIndex; i < endIndex; i++) {
                decompressedData.append(decompressedData.charAt(i));
            }

            if (trio.nextChar != '\0') {
                decompressedData.append(trio.nextChar);
            }
        }

        return decompressedData.toString();
    }

    private byte[] convertToBytes(ArrayList<Trio> compressedData) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        for (Trio trio : compressedData) {
            dataOutputStream.writeInt(trio.offset);
            dataOutputStream.writeInt(trio.length);
            dataOutputStream.writeChar(trio.nextChar);
        }

        dataOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    private ArrayList<Trio> convertFromBytes(byte[] bytes) throws IOException {
        ArrayList<Trio> compressedData = new ArrayList<>();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        while (dataInputStream.available() > 0) {
            int offset = dataInputStream.readInt();
            int length = dataInputStream.readInt();
            char nextChar = dataInputStream.readChar();
            compressedData.add(new Trio(offset, length, nextChar));
        }

        dataInputStream.close();
        return compressedData;
    }

    public void compress (String file_path_input , String file_path_to_archive){

        StringBuilder inputData = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file_path_input));
            String line;
            while ((line = reader.readLine()) != null) {
                inputData.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        ArrayList<Trio> compressedData = compress(inputData.toString() , 500);


        try {
            byte[] compressedBytes = convertToBytes(compressedData);
            FileOutputStream fileOutputStream = new FileOutputStream(file_path_to_archive);
            fileOutputStream.write(compressedBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decompress (String file_path_output , String file_path_to_archive){

        try {
            FileInputStream fileInputStream = new FileInputStream(file_path_to_archive);
            byte[] readBytes = new byte[fileInputStream.available()];
            fileInputStream.read(readBytes);
            fileInputStream.close();

            ArrayList<Trio> readCompressedData = convertFromBytes(readBytes);
            String decompressedData = decompress(readCompressedData);


            BufferedWriter writer = new BufferedWriter(new FileWriter(file_path_output));
            writer.write(decompressedData);
            writer.close();
        } catch (Exception e){
            System.out.println("erorr");
        }
    }

}