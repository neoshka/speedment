/**
 *
 * Copyright (c) 2006-2016, Speedment, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.speedment.maven.abstractmojo;

import com.speedment.generator.GeneratorBundle;
import com.speedment.maven.typemapper.Mapping;
import com.speedment.generator.component.TypeMapperComponent;
import com.speedment.generator.internal.component.CodeGenerationComponentImpl;
import com.speedment.internal.common.injector.InjectBundle;
import com.speedment.maven.component.MavenPathComponent;
import static com.speedment.internal.common.injector.State.INITIALIZED;
import static com.speedment.internal.common.injector.State.RESOLVED;
import com.speedment.internal.common.injector.annotation.ExecuteBefore;
import com.speedment.internal.common.injector.annotation.InjectKey;
import com.speedment.internal.common.injector.annotation.WithState;
import com.speedment.internal.common.injector.internal.InjectorImpl;
import com.speedment.internal.common.logger.Level;
import com.speedment.internal.common.logger.LoggerManager;
import com.speedment.runtime.ApplicationBuilder;
import com.speedment.runtime.Speedment;
import com.speedment.runtime.component.Component;
import com.speedment.runtime.config.mapper.TypeMapper;
import com.speedment.runtime.internal.DefaultApplicationBuilder;
import com.speedment.runtime.internal.DefaultApplicationMetadata;
import com.speedment.runtime.internal.EmptyApplicationMetadata;
import com.speedment.tool.internal.component.UserInterfaceComponentImpl;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

import static com.speedment.runtime.internal.DefaultApplicationMetadata.METADATA_LOCATION;
import com.speedment.tool.ToolBundle;
import static com.speedment.tool.internal.util.ConfigFileHelper.DEFAULT_CONFIG_LOCATION;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.maven.project.MavenProject;

/**
 * The abstract base implementation for all the Speedment Mojos.
 *
 * @author Emil Forslund
 */
public abstract class AbstractSpeedmentMojo extends AbstractMojo {

    private final static File DEFAULT_CONFIG = new File(DEFAULT_CONFIG_LOCATION);
    private final static Consumer<ApplicationBuilder<?, ?>> NOTHING = (builder) -> {};

    private final Consumer<ApplicationBuilder<?, ?>> configurer;
    
    protected abstract MavenProject project();
    protected abstract boolean debug();
    protected abstract String dbmsHost();
    protected abstract int dbmsPort();
    protected abstract String dbmsUsername();
    protected abstract String dbmsPassword();
    protected abstract File configLocation();
    protected abstract String[] components();
    protected abstract Mapping[] typeMappers();
    protected abstract String launchMessage();
    protected abstract void execute(Speedment speedment) throws MojoExecutionException, MojoFailureException;

    protected AbstractSpeedmentMojo() {this(NOTHING);}
    
    protected AbstractSpeedmentMojo(Consumer<ApplicationBuilder<?, ?>> configurer) {
        this.configurer = configurer;
    }

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        if (debug()) {
            LoggerManager.getLogger(InjectorImpl.class).setLevel(Level.DEBUG);
        }

        final ApplicationBuilder<?, ?> builder = createBuilder();
        builder.withInjectable(MavenPathComponent.class);
        configurer.accept(builder);
        final Speedment speedment = builder.build();
        speedment.getOrThrow(MavenPathComponent.class).setMavenProject(project());

        getLog().info(launchMessage());
        execute(speedment);
    }
    
    /**
     * Returns {@code true} if the default configuration file is non-null,
     * exists and is readable. If, not, {@code false} is returned and an
     * appropriate message is shown in the console.
     *
     * @return {@code true} if available, else {@code false}
     */
    protected final boolean hasConfigFile() {
        return hasConfigFile(configLocation());
    }

    /**
     * Returns if the specified file is non-null, exists and is readable. If,
     * not, {@code false} is returned and an appropriate message is shown in the
     * console.
     *
     * @param file the config file to check
     * @return {@code true} if available, else {@code false}
     */
    protected final boolean hasConfigFile(File file) {
        if (file == null) {
            final String msg = "The expected .json-file is null.";
            getLog().info(msg);
            return false;
        } else if (!file.exists()) {
            final String msg = "The expected .json-file '" + file.getAbsolutePath() + "' does not exist.";
            getLog().info(msg);
            return false;
        } else if (!file.canRead()) {
            final String err = "The expected .json-file '" + file.getAbsolutePath() + "' is not readable.";
            getLog().error(err);
            return false;
        } else {
            return true;
        }
    }

    private ApplicationBuilder<?, ?> createBuilder() throws MojoExecutionException {
        ApplicationBuilder<?, ?> result;

        // Configure config file location
        if (hasConfigFile()) {
            result = new DefaultApplicationBuilder(DefaultApplicationMetadata.class)
                .withParam(METADATA_LOCATION, configLocation().getAbsolutePath());
        } else if (hasConfigFile(DEFAULT_CONFIG)) {
            result = new DefaultApplicationBuilder(DefaultApplicationMetadata.class)
                .withParam(METADATA_LOCATION, DEFAULT_CONFIG_LOCATION);
        } else {
            result = new DefaultApplicationBuilder(EmptyApplicationMetadata.class);
        }

        // Configure manual database settings
        if (dbmsHost() != null) {
            result = result.withIpAddress(dbmsHost());
            getLog().info("Custom database host '" + dbmsHost() + "'.");
        }

        if (dbmsPort() != 0) {
            result = result.withPort(dbmsPort());
            getLog().info("Custom database port '" + dbmsPort() + "'.");
        }

        if (dbmsUsername() != null) {
            result = result.withUsername(dbmsUsername());
            getLog().info("Custom database username '" + dbmsUsername() + "'.");
        }

        if (dbmsPassword() != null) {
            result = result.withPassword(dbmsPassword());
            getLog().info("Custom database password '********'.");
        }

        // Add mandatory components that are not included in 'runtime'
        result
            .withBundle(GeneratorBundle.class)
            .withBundle(ToolBundle.class);

        result.with(CodeGenerationComponentImpl.class);
        result.with(UserInterfaceComponentImpl.class);
        result.with(MavenPathComponent.class);

        // Add any extra type mappers requested by the user
        TypeMapperInstaller.mappings = typeMappers(); // <-- Hack to pass type mappers to class with default constructor.
        result.withInjectable(TypeMapperInstaller.class);

        // Add extra components requested by the user
        final String[] components = components();
        if (components != null) {
            for (final String component : components) {
                try {
                    final Class<?> uncasted = Class.forName(component);

                    if (Component.class.isAssignableFrom(uncasted)) {
                        @SuppressWarnings("unchecked")
                        final Class<? extends Component> casted
                            = (Class<? extends Component>) uncasted;
                        result.with(casted);
                    } else if (InjectBundle.class.isAssignableFrom(uncasted)) {
                        @SuppressWarnings("unchecked")
                        final Class<? extends InjectBundle> casted
                            = (Class<? extends InjectBundle>) uncasted;
                        result.withBundle(casted);
                    } else {
                        result.withInjectable(uncasted);
                    }

                } catch (final ClassNotFoundException ex) {
                    throw new MojoExecutionException(
                        "Specified class '" + component + "' could not be "
                        + "found on class path. Has the dependency been "
                        + "configured properly?", ex
                    );
                }
            }
        }

        // Return the resulting builder.
        return result;
    }

    private final static class TypeMapperInstantiationException extends RuntimeException {

        private static final long serialVersionUID = -8267239306656063289L;

        private TypeMapperInstantiationException(Throwable thrw) {
            super(thrw);
        }
    }

    @InjectKey(TypeMapperInstaller.class)
    private final static class TypeMapperInstaller {

        private static Mapping[] mappings;

        @ExecuteBefore(RESOLVED)
        void installInTypeMapper(@WithState(INITIALIZED) TypeMapperComponent typeMappers) throws MojoExecutionException {
            if (mappings != null) {
                for (final Mapping mapping : mappings) {
                    final Class<?> databaseType;
                    try {
                        final Class<?> casted = Class.forName(mapping.getDatabaseType());
                        databaseType = casted;
                    } catch (final ClassNotFoundException ex) {
                        throw new MojoExecutionException(
                            "Specified database type '" + mapping.getDatabaseType() + "' "
                            + "could not be found on class path. Make sure it is a "
                            + "valid JDBC type for the choosen connector.", ex
                        );
                    } catch (final ClassCastException ex) {
                        throw new MojoExecutionException(
                            "An unexpected ClassCastException occured.", ex
                        );
                    }

                    try {
                        final Class<?> uncasted = Class.forName(mapping.getImplementation());

                        @SuppressWarnings("unchecked")
                        final Class<TypeMapper<?, ?>> casted = (Class<TypeMapper<?, ?>>) uncasted;
                        final Constructor<TypeMapper<?, ?>> constructor = casted.getConstructor();
                        final Supplier<TypeMapper<?, ?>> supplier = () -> {
                            try {
                                return constructor.newInstance();
                            } catch (final IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
                                throw new TypeMapperInstantiationException(ex);
                            }
                        };

                        typeMappers.install(databaseType, supplier);
                    } catch (final ClassNotFoundException ex) {
                        throw new MojoExecutionException(
                            "Specified class '" + mapping.getImplementation() + "' could not be "
                            + "found on class path. Has the dependency been "
                            + "configured properly?", ex
                        );
                    } catch (final ClassCastException ex) {
                        throw new MojoExecutionException(
                            "Specified class '" + mapping.getImplementation()
                            + "' does not implement the '"
                            + TypeMapper.class.getSimpleName() + "'-interface.", ex
                        );
                    } catch (final NoSuchMethodException | TypeMapperInstantiationException ex) {
                        throw new MojoExecutionException(
                            "Specified class '" + mapping.getImplementation()
                            + "' could not be instantiated. Does it have a default "
                            + "constructor?", ex
                        );
                    }
                }
            }
        }
    }
}
