/*
 Copyright 2013 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.lightblue.mongo.test;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.UnknownHostException;

import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.mongodb.MongoClient;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

/**
 * <p>JUnit {@link ExternalResource} that will handle standing/shutting down an In-Memory Mongo instance
 * for testing purposes.</p>
 * <p>Example Usage:<br>
 *   Create an instance of MongoServerExternalResource with a {@literal @}Rule annotation.
 *   <p><code>
 *      {@literal @}Rule<br>
 *      public MongoServerExternalResource mongoServer = new MongoServerExternalResource();
 *   </code></p>
 *   Then set the <code>{@literal @}InMemoryMongoServer</code> annotation on either the Class or Method
 *   level. This annotation allows properties of the Mongo instance to be configured, but is required even
 *   if only using the default values.
 * </p>
 *
 * @author dcrissman
 */
public class MongoServerExternalResource extends ExternalResource{

    public static final int DEFAULT_PORT = 27777;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Inherited
    @Documented
    public @interface InMemoryMongoServer {
        /** Port to run the Mongo instance on.*/
        int port() default DEFAULT_PORT;
        /** Version of Mongo to use. */
        Version version() default Version.V2_6_1;
    }

    private InMemoryMongoServer immsAnnotation = null;
    private MongodExecutable mongodExe;
    private MongodProcess mongod;

    @Override
    public Statement apply(Statement base, Description description){
        immsAnnotation = description.getAnnotation(InMemoryMongoServer.class);
        if((immsAnnotation == null) && description.isTest()){
            immsAnnotation = description.getTestClass().getAnnotation(InMemoryMongoServer.class);
        }

        if(immsAnnotation == null){
            throw new IllegalStateException("@InMemoryMongoServer must be set on suite or test level.");
        }

        return super.apply(base, description);
    }

    @Override
    protected void before() throws UnknownHostException, IOException{
        MongodStarter runtime = MongodStarter.getDefaultInstance();
        IMongodConfig config = new MongodConfigBuilder().
                version(immsAnnotation.version()).
                net(new Net(immsAnnotation.port(), Network.localhostIsIPv6())).
                build();
        mongodExe = runtime.prepare(config);
        mongod = mongodExe.start();
    }

    @Override
    protected void after(){
        if (mongod != null) {
            mongod.stop();
            mongodExe.stop();
        }
    }

    /**
     * Provides a {@link MongoClient} for the running in-memory Mongo instance.
     * @return {@link MongoClient}
     * @throws UnknownHostException
     */
    public MongoClient getConnection() throws UnknownHostException{
        return new MongoClient("localhost", immsAnnotation.port());
    }

}
