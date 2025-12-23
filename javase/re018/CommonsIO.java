package javase.re018;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class CommonsIO {
    /*
     * IOUtils.copy(InputStream, OutputStream) 流复制（字节 / 字符流均可） 复制文件流、网络流
     * IOUtils.toString(InputStream, Charset) 流转字符串 读取文件流为字符串
     * IOUtils.readLines(InputStream, Charset) 流按行读取为 List<String> 按行读取文本流
     * IOUtils.write(String, OutputStream, Charset) 字符串写入流 向文件流写字符串
     * IOUtils.closeQuietly(Closeable) 静默关闭流（无需 try-catch） 关闭流时忽略异常
     * 
     * FileUtils.copyFile(File, File) 复制文件 快速复制文件
     * FileUtils.copyDirectory(File, File) 复制目录（含子文件） 复制文件夹
     * FileUtils.deleteDirectory(File) 删除目录（含所有内容） 递归删除文件夹
     * FileUtils.readFileToString(File, Charset) 读取文件为字符串 一行读取文本文件
     * FileUtils.writeStringToFile(File, String, Charset) 字符串写入文件 一行写入文本文件
     * FileUtils.readLines(File, Charset) 按行读取文件为 List<String> 按行读取文本
     * FileUtils.forceMkdir(File) 创建目录（含父目录，失败抛异常） 替代mkdirs()，更严格
     * FileUtils.listFiles(File, String[], boolean) 遍历目录下的指定类型文件 查找所有.txt 文件
     * 
     * FilenameUtils.getName(String) 获取文件名（含后缀） a/b/c.txt → c.txt
     * FilenameUtils.getBaseName(String) 获取文件名（不含后缀） a/b/c.txt → c
     * FilenameUtils.getExtension(String) 获取文件后缀 a/b/c.txt → txt
     * FilenameUtils.concat(String, String) 拼接路径（适配系统分隔符） 拼接a/b和c.txt → a/b/c.txt
     * FilenameUtils.normalize(String) 标准化路径（处理../.） a/../b → b
     */
    public static void main(String[] args) throws Exception {
        // 1. 流复制：复制文件（字节流）
        try (FileInputStream fis = new FileInputStream("source.txt");
                FileOutputStream fos = new FileOutputStream("target.txt")) {
            IOUtils.copy(fis, fos); // 替代原生的字节数组循环读写
        }

        // 2. 流转字符串：读取文件为字符串
        try (FileInputStream fis = new FileInputStream("source.txt")) {
            String content = IOUtils.toString(fis, StandardCharsets.UTF_8);
            System.out.println("文件内容：" + content);
        }

        // 3. 按行读取流：返回List<String>
        try (FileInputStream fis = new FileInputStream("source.txt")) {
            List<String> lines = IOUtils.readLines(fis, StandardCharsets.UTF_8);
            lines.forEach(line -> System.out.println("行内容：" + line));
        }

        // 4. 字符串写入流
        try (FileOutputStream fos = new FileOutputStream("target.txt")) {
            IOUtils.write("Commons IO 写入的内容", fos, StandardCharsets.UTF_8);
        }

        // 5. 静默关闭流（旧版Commons IO，Java 7+推荐try-with-resources）
        // IOUtils.closeQuietly(fis); // 无需try-catch，自动处理null和异常
        File sourceFile = new File("source.txt");
        File targetFile = new File("target.txt");
        File sourceDir = new File("sourceDir");
        File targetDir = new File("targetDir");

        // 1. 复制文件
        FileUtils.copyFile(sourceFile, targetFile);

        // 2. 复制目录（含子文件）
        FileUtils.copyDirectory(sourceDir, targetDir);

        // 3. 读取文件为字符串
        String content = FileUtils.readFileToString(sourceFile, StandardCharsets.UTF_8);
        System.out.println("文件内容：" + content);

        // 4. 字符串写入文件（覆盖），追加用writeStringToFile(File, String, Charset, boolean)
        FileUtils.writeStringToFile(targetFile, "Commons IO 写入的文件内容", StandardCharsets.UTF_8);

        // 5. 按行读取文件
        List<String> lines = FileUtils.readLines(sourceFile, StandardCharsets.UTF_8);
        lines.forEach(System.out::println);

        // 6. 创建目录（含父目录）
        File newDir = new File("parent/child/grandchild");
        FileUtils.forceMkdir(newDir); // 替代newDir.mkdirs()，目录创建失败会抛异常

        // 7. 遍历目录下的所有.txt文件（递归子目录）
        Collection<File> txtFiles = FileUtils.listFiles(sourceDir, new String[] { "txt" }, true);
        txtFiles.forEach(file -> System.out.println("txt文件：" + file.getAbsolutePath()));

        // 8. 删除目录（含所有内容）
        FileUtils.deleteDirectory(targetDir);
        // 删除单个文件
        FileUtils.delete(targetFile);

        String path = "D:/dev/project/test.txt";
        String unixPath = "/home/user/test.txt";
        String relativePath = "a/../b/c.txt";

        // 1. 获取文件名（含后缀）
        System.out.println("文件名：" + FilenameUtils.getName(path)); // test.txt

        // 2. 获取文件名（不含后缀）
        System.out.println("文件前缀：" + FilenameUtils.getBaseName(path)); // test

        // 3. 获取文件后缀
        System.out.println("文件后缀：" + FilenameUtils.getExtension(path)); // txt

        // 4. 拼接路径
        String concatPath = FilenameUtils.concat("D:/dev", "project/test.txt");
        System.out.println("拼接路径：" + concatPath); // D:/dev/project/test.txt

        // 5. 标准化路径
        String normalizePath = FilenameUtils.normalize(relativePath);
        System.out.println("标准化路径：" + normalizePath); // b/c.txt
    }
}
