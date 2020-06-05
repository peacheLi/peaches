package ch.frankel.minify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "minify", defaultPhase = LifecyclePhase.PREPARE_PACKAGE)
public class MinifyMojo extends AbstractMojo {

    @Parameter(defaultValue = "src/main/webapp", required = true)
    private File webappDirectory;

    @Parameter(defaultValue = "${project.build.directory}/${project.build.finalName}", required = true)
    private File targetDirectory;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            Files.walkFileTree(
                    webappDirectory.toPath(),
                    new MinifyVisitor(webappDirectory.toPath(), targetDirectory.toPath(), getLog())
            );
        } catch (IOException e) {
            throw new MojoExecutionException("Exception during execution", e);
        }
    }
}