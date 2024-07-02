package eu.eurekabpo.maven.enforcer.rules;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.RepositoryUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.enforcer.rule.api.AbstractEnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleError;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;

import lombok.Getter;
import lombok.Setter;

/**
 * Rule to ensure artifact has received from specified repository.
 *
 */
@Named("requireDependencyRepository")
public class RequireDependencyRepository extends AbstractEnforcerRule {

    @Setter @Getter
    private String repositoryId;
    @Setter @Getter
    private String groupId;
    @Setter @Getter
    private String artifactId;
    @Inject
    private MavenProject project;
    @Inject
    private MavenSession session;
    @Inject
    private RepositorySystem repositorySystem;

    @Override
    public void execute() throws EnforcerRuleException {
        if (this.repositoryId == null) {
            throw new EnforcerRuleError("Repository id unspecified");
        }
        if (project.getRepositories().stream().noneMatch(repo -> Objects.equals(repo.getId(), repositoryId))) {
            throw new EnforcerRuleError("Specified Repository id (" + repositoryId +
                ") does not match with any declared repository " +
                project.getRepositories().stream().map( repo -> repo.getId()).collect(Collectors.toList()));
        }
        checkDependencies();
    }

    private void checkDependencies() throws EnforcerRuleException {
        List<RemoteRepository> repository = project.getRemoteProjectRepositories().stream().filter(repo -> repositoryId.equals(repo.getId())).collect(Collectors.toList());
        Artifact artifact = project.getArtifacts().stream().filter(dep -> Objects.equals(dep.getArtifactId(), artifactId) && Objects.equals(dep.getGroupId(), groupId)).findFirst()
            .orElseThrow(() -> new EnforcerRuleError("Artifact " + groupId + ":" + artifactId + " is not a project dependency"));
        try {
            FileUtils.deleteDirectory(findArtifactVersionLocal(artifact));
            ArtifactRequest request = new ArtifactRequest(RepositoryUtils.toArtifact(artifact), repository, null);
            repositorySystem.resolveArtifact(session.getRepositorySession(), request);
        } catch (ArtifactResolutionException e) {
            throw new EnforcerRuleException("Maven artifact " + toString(artifact) + " has not found in repository " + repositoryId);
        } catch (IOException e) {
            throw new EnforcerRuleException("Cannot remove artifact file " + toString(artifact));
        }
    }

    private String toString(Artifact artifact) {
        return String.join(":", artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }

    private File findArtifactVersionLocal(Artifact artifact) {
        ArtifactRepository localRepository = session.getLocalRepository();
        File artifactFile = new File(localRepository.getBasedir(), localRepository.pathOf(artifact));
        return artifactFile.getParentFile();
    }
}
