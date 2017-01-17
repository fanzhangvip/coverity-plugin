/*******************************************************************************
 * Copyright (c) 2017 Synopsys, Inc
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Synopsys, Inc - initial implementation and documentation
 *******************************************************************************/
package jenkins.plugins.coverity.CoverityTool;

import jenkins.plugins.coverity.CoverityPublisher;
import jenkins.plugins.coverity.CoverityPublisherBuilder;
import jenkins.plugins.coverity.InvocationAssistance;
import jenkins.plugins.coverity.InvocationAssistanceBuilder;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CovEmitJavaCommandTest extends CommandTestBase {

    @Test
    public void addJavaWarFilesTest() throws IOException, InterruptedException {
        List<String> javaWarFiles = new ArrayList<>();
        javaWarFiles.add("webapp1.war");
        javaWarFiles.add("webapp2.war");

        InvocationAssistance invocationAssistance = new InvocationAssistanceBuilder().withJavaWarFilesNames(javaWarFiles).build();
        CoverityPublisher publisher = new CoverityPublisherBuilder().withInvocationAssistance(invocationAssistance).build();

        Command covEmitJavaCommand = new CovEmitJavaCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, false);
        setExpectedArguments(new String[] {"cov-emit-java", "--dir", "TestDir", "--webapp-archive", "webapp1.war", "--webapp-archive", "webapp2.war"});
        covEmitJavaCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] cov-emit-java command line arguments: " + actualArguments.toString());
    }

    @Test
    public void cannotExecuteTest() throws IOException, InterruptedException {
        CoverityPublisher publisher = new CoverityPublisherBuilder().build();

        Command covEmitJavaCommand = new CovEmitJavaCommand(build, launcher, listener, publisher, StringUtils.EMPTY, envVars, false);
        covEmitJavaCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can't be executed");
    }
}
