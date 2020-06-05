package ch.frankel.minify;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.List;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.apache.maven.plugin.logging.Log;

public class MinifyVisitor implements FileVisitor<Path> {

    private final Path webappDirectory;
    private final Path targetDirectory;
    private final Log log;
    private final HtmlCompressor compressor = new HtmlCompressor();
    private final PathMatcher matcher =
            FileSystems.getDefault().getPathMatcher("glob:**.{html,jsp,ftl}");

    public MinifyVisitor(Path webappDirectory, Path targetDirectory, Log log) {
        this.webappDirectory = webappDirectory;
        this.targetDirectory = targetDirectory;
        this.log = log;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        log.info("Evaluating whether " + file + " should be processed");
        if (matcher.matches(file)) {
            log.info("Found " + file + " to be processed");
            List<String> lines = Files.readAllLines(file);
            String compressedContent = compressor.compress(String.join("", lines));
            log.info("compress content:" + compressedContent);
            Path relativePath = webappDirectory.relativize(file);
            log.info("Relative path is " + relativePath);
            Path target = Paths.get(targetDirectory.toUri()).resolve(relativePath);
            Path parentDir = target.getParent();
            Files.createDirectories(parentDir);
            log.info("Target directory created " + parentDir);
            Files.write(target, Collections.singletonList(compressedContent));
            log.info("Target file created " + target);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) {
        if (e != null) {
            log.error(e);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException e) {
        if (e != null) {
            log.error(e);
        }
        return FileVisitResult.CONTINUE;
    }
}