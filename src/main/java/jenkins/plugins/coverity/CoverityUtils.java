/*******************************************************************************
 * Copyright (c) 2014 Coverity, Inc

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Coverity, Inc - initial implementation and documentation
 *******************************************************************************/
package jenkins.plugins.coverity;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.*;
import hudson.EnvVars;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CoverityUtils {


    static EnvVars envVars;
    
	
	public static String evaluateEnvVars(String input, AbstractBuild build, TaskListener listener)throws RuntimeException{
		
		try{
			envVars = build.getEnvironment(listener);
			return envVars.expand(input);
		}catch(Exception e){
			throw new RuntimeException("Error trying to evaluate environment variable: " + input);
		}
	}

	public static List<String> evaluateEnvVars(List<String> input, AbstractBuild build, TaskListener listener)throws RuntimeException{
		List<String> output = new ArrayList<String>();
		
		try{
			envVars = build.getEnvironment(listener);	
			
			for(String cmd : input){
                /**
                 * Fix bug 78168
                 * After evaluating an environment variable, we need to check if more than one options where specified
                 * on it. In order to do so, we use the command split(" ").
                 */
                cmd = envVars.expand(cmd).trim().replaceAll(" +", " ");
                Collections.addAll(output, cmd.split(" "));
			}
		}catch(Exception e){
			throw new RuntimeException("Error trying to evaluate Environment variables in: " + input.toString() );
		}

		return output;

	}

	public static String evaluateEnvVars(String input, EnvVars environment)throws RuntimeException{
		
		try{
			return environment.expand(input);
		}catch(Exception e){
			throw new RuntimeException("Error trying to evaluate environment variable: " + input);
		}
	}

	public static List<String> evaluateEnvVars(List<String> input, EnvVars environment)throws RuntimeException{
		List<String> output = new ArrayList<String>();
		
		try{
				
			for(String cmd : input){
				output.add(environment.expand(cmd));
			}
		}catch(Exception e){
			throw new RuntimeException("Error trying to evaluate Environment variables in: " + input.toString() );
		}

		return output;

	}


	public static void setEnvVars(EnvVars environment){
		envVars = environment;
	}

    public static void checkDir(VirtualChannel channel, String home) throws Exception {
		FilePath homePath = new FilePath(channel, home);
        if(!homePath.exists()){
            throw new Exception("Directory: " + home + " doesn't exist.");
        }
    }

	/**
	 * getCovBuild
	 *
	 * Retrieves the location of cov-build executable/sh from the system and returns the string of the
	 * path
	 * @return  string of cov-build's path
	 */
	public static String getCovBuild(TaskListener listener, Node node) {
		Executor executor = Executor.currentExecutor();
		Queue.Executable exec = executor.getCurrentExecutable();
		AbstractBuild build = (AbstractBuild) exec;
		AbstractProject project = build.getProject();
		CoverityPublisher publisher = (CoverityPublisher) project.getPublishersList().get(CoverityPublisher.class);
		InvocationAssistance ii = publisher.getInvocationAssistance();

		String covBuild = "cov-build";
		String home = null;
		try {
			home = publisher.getDescriptor().getHome(node, build.getEnvironment(listener));
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		if(ii != null){
			if(ii.getSaOverride() != null) {
				try {
					home = new CoverityInstallation(ii.getSaOverride()).forEnvironment(build.getEnvironment(listener)).getHome();
					CoverityUtils.checkDir(node.getChannel(), home);
				} catch(IOException e) {
					e.printStackTrace();
				} catch(InterruptedException e) {
					e.printStackTrace();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(home != null) {
			covBuild = new FilePath(node.getChannel(), home).child("bin").child(covBuild).getRemote();
		}

		return covBuild;
	}

}