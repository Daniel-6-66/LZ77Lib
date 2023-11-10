import org.example.LZ77;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class LZ77Test {

    @Test
    public void testCompressAndDecompress() {
        LZ77 lz77 = new LZ77();
        String inputFilePath = "C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77Lib\\src\\test\\java\\input";
        String archiveFilePath = "C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77Lib\\src\\test\\java\\archive";
        String outputFilePath = "C:\\Users\\BoSS JR\\OneDrive\\Рабочий стол\\Учёба\\LZ77Lib\\src\\test\\java\\output";

        // Создаем тестовые данные в файле input.txt
        createTestInputFile(inputFilePath);

        // Тестируем сжатие
        lz77.compress(inputFilePath, archiveFilePath);

        // Тестируем распаковку
        lz77.decompress(outputFilePath, archiveFilePath);

        // Сравниваем содержимое оригинального и распакованного файлов
        try {
            BufferedReader originalReader = new BufferedReader(new FileReader(inputFilePath));
            BufferedReader decompressedReader = new BufferedReader(new FileReader(outputFilePath));

            String originalLine = null;
            String decompressedLine = null;

            // Чтение первых строк из файлов
            if ((originalLine = originalReader.readLine()) != null && (decompressedLine = decompressedReader.readLine()) != null) {
                // Проверка, что файлы не пустые
                assertTrue("Original file is not empty", originalLine != null);
                assertTrue("Decompressed file is not empty", decompressedLine != null);

                // Сравниваем строки в цикле
                while (originalLine != null && decompressedLine != null) {
                    assertEquals(originalLine, decompressedLine);
                    originalLine = originalReader.readLine();
                    decompressedLine = decompressedReader.readLine();
                }
            } else {
                // Если один из файлов пустой, проверяем, что оба пусты
                assertTrue("Both files are empty", originalLine == null && decompressedLine == null);
            }

            originalReader.close();
            decompressedReader.close();

        } catch (IOException e) {
            fail("IOException during file comparison: " + e.getMessage());
        }
    }

    private void createTestInputFile(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("This is a test file. It contains some text for compression and decompression testing. Feel free to modify it for additional testing.");
        } catch (IOException e) {
            fail("IOException during test file creation: " + e.getMessage());
        }
    }
}
