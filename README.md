## requireDependencyRepository

This project presents custom rule requireDependencyRepository for [maven-enforcer-plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/index.html).

#### Purpose
requireDependencyRepository is meant to ensure that specified artifact is received from specified repository.

#### Minimum requirements
- Java 8
- Maven 3.6.3
- Maven Enforcer Plugin 3.2.1

#### Usage

Example of usage.

Add such declaration to your pom.xml:

```xml
<build>
  <plugins>
    ...
    <plugin>
      <artifactId>maven-enforcer-plugin</artifactId>
      <executions>
        <execution>
          <id>enforce-dependency-repository</id>
          <goals>
            <goal>enforce</goal>
          </goals>
          <configuration>
            <rules>
              <requireDependencyRepository>
                <repositoryId>YOUR REPOSITORY ID</repositoryId>
                <groupId>YOUR GROUP ID</groupId>
                <artifactId>YOUR ARTIFACT ID</artifactId>
              </requireDependencyRepository>
            </rules>
          </configuration>
        </execution>
      </executions>
      <dependencies>
        <dependency>
          <groupId>eu.eureka-bpo.maven</groupId>
          <artifactId>require-dependency-repository</artifactId>
          <version>1.0.0</version>
        </dependency>
      </dependencies>
    </plugin>
  ...
  </plugins>
</build>
```
