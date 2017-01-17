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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PostCovAnalyzeCommandTest extends CommandTestBase {

    @Test
    public void prepareCommandTest() throws IOException, InterruptedException {
        InvocationAssistance invocationAssistance = new InvocationAssistanceBuilder().
                withUsingPostAnalyzeCmnd(true).
                withPostAnalyzeCmd("TestPostAnalyzeCommand").build();
        CoverityPublisher publisher = new CoverityPublisherBuilder().withInvocationAssistance(invocationAssistance).build();

        Command postCovAnalyzeCommand = new PostCovAnalyzeCommand(build, launcher, listener, publisher, envVars);
        setExpectedArguments(new String[] {"TestPostAnalyzeCommand"});
        postCovAnalyzeCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] post cov-analyze command line arguments: " + actualArguments.toString());
    }

    @Test
    public void prepareCommandTest_WithParseException() throws IOException, InterruptedException {
        InvocationAssistance invocationAssistance = new InvocationAssistanceBuilder().
                withUsingPostAnalyzeCmnd(true).
                withPostAnalyzeCmd("\'").build();
        CoverityPublisher publisher = new CoverityPublisherBuilder().withInvocationAssistance(invocationAssistance).build();

        Command postCovAnalyzeCommand = new PostCovAnalyzeCommand(build, launcher, listener, publisher, envVars);
        try{
            postCovAnalyzeCommand.runCommand();
            fail("RuntimeException should have been thrown");
        }catch(RuntimeException e) {
            assertEquals("ParseException occurred during tokenizing the post cov-analyze command.", e.getMessage());
        }
    }

    @Test
    public void cannotExeucteTest() throws IOException, InterruptedException {
        CoverityPublisher publisher = new CoverityPublisherBuilder().build();

        Command postCovAnalyzeCommand = new PostCovAnalyzeCommand(build, launcher, listener, publisher, envVars);
        postCovAnalyzeCommand.runCommand();
        consoleLogger.verifyLastMessage("[Coverity] Skipping command because it can't be executed");
    }
}
