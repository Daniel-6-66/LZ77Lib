package org.example;

import java.io.*;
import java.util.ArrayList;

class LZ77 {
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

    public ArrayList<Trio> compress(String input, int windowSize) {
        ArrayList<Trio> compressedData = new ArrayList<>();
        int lookAheadBufferSize = windowSize;
        int searchBufferStart = 0;

        while (searchBufferStart < input.length()) {
            int maxLength = 0;
            int offset = 0;

            for (int i = Math.max(0, searchBufferStart - windowSize); i < searchBufferStart; i++) {
                int length = 0;
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


    public String decompress(ArrayList<Trio> compressedData) {
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

    public byte[] convertToBytes(ArrayList<Trio> compressedData) throws IOException {
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

    public ArrayList<Trio> convertFromBytes(byte[] bytes) throws IOException {
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
    public static void main(String[] args) {
        LZ77 lz77 = new LZ77();

        // Чтение данных из input.txt
        StringBuilder inputData = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77_algoritm\\src\\input.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                inputData.append(line);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Сжатие данных
        ArrayList<Trio> compressedData = lz77.compress(inputData.toString() , 500);
        for (int i = 0; i < compressedData.size();i++){
            System.out.println("<"+compressedData.get(i).length+","+compressedData.get(i).offset+","+compressedData.get(i).nextChar+">");
        }
        // Преобразование сжатых данных в байты и запись в архивный файл
        try {
            byte[] compressedBytes = lz77.convertToBytes(compressedData);
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77_algoritm\\src\\archive.txt");
            fileOutputStream.write(compressedBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Чтение сжатых данных из архивного файла
        try {
            FileInputStream fileInputStream = new FileInputStream("C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77_algoritm\\src\\archive.txt");
            byte[] readBytes = new byte[fileInputStream.available()];
            fileInputStream.read(readBytes);
            fileInputStream.close();

            // Обратное преобразование из байтов и распаковка данных
            ArrayList<Trio> readCompressedData = lz77.convertFromBytes(readBytes);
            String decompressedData = lz77.decompress(readCompressedData);

            // Запись распакованных данных в output.txt
            BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77_algoritm\\src\\output.txt"));
            writer.write(decompressedData);
            writer.close();
        } catch (Exception e){
            System.out.println("erorr");
        }
    }
}