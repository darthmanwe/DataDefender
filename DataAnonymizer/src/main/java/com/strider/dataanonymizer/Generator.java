/*
 * 
 * Copyright 2014, Armenak Grigoryan, Matthew Eaton and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package com.strider.dataanonymizer;

import static com.strider.dataanonymizer.utils.AppProperties.loadProperties;
import static org.apache.log4j.Logger.getLogger;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.strider.dataanonymizer.database.IDBFactory;

/**
 * Entry point to Data Generator.
 *  
 * This class will parse and analyze the parameters and execute appropriate 
 * service.
 *
 */
public class Generator {

    private static final Logger log = getLogger(Generator.class);

    public static void main( String[] args ) throws ParseException, AnonymizerException {

        if (args.length == 0 ) {
            log.info("To display usage info please type");
            log.info("    java -jar DataAnonymizer.jar com.strider.dataanonymizer.Generator --help");
            return;
        }

        final Options options = createOptions();
        final CommandLine line = getCommandLine(options, args);
        if (line.hasOption("help")) {
            help(options);
            return;
        }

        String databasePropertyFile = "db.properties";
        if (line.hasOption("P")) {
            databasePropertyFile = line.getOptionValues("P")[0];
        }
        Properties props = loadProperties(databasePropertyFile);

        String anonymizerPropertyFile = "anonymizer.properties";
        if (line.hasOption("A")) {
            anonymizerPropertyFile = line.getOptionValue("A");
        }
        Properties anonymizerProperties = loadProperties(anonymizerPropertyFile);

        IGenerator generator = new DataGenerator();
        if (line.hasOption("g")) {
            IDBFactory dbFactory = IDBFactory.get(props);
            generator.generate(dbFactory, anonymizerProperties);
        }
    }

    /**
     * Parses command line arguments
     * @param options
     * @param args
     * @return CommandLine
     * @throws com.strider.dataanonymizer.AnonymizerException
     */
    private static CommandLine getCommandLine(final Options options, final String[] args) 
    throws ParseException {
        final CommandLineParser parser = new GnuParser();
        CommandLine line = null;
 
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            help(options);
        }
 
        return line;
    }    
    
    /**
     * Creates options for the command line
     * 
     * @return Options
     */
    private static Options createOptions() {
        final Options options = new Options();
        options.addOption( "h", "help", false, "Display help");        
        options.addOption( "g", "generate", false, "generate data" );
        options.addOption( "A", "anonymizer-properties", true, "define anonymizer property file" );
        options.addOption( "P", "database-properties", true, "define database property file" );
        return options;
    }
 
    /**
     * Displays help
     * 
     * @param Options 
     */
    private static void help(final Options options) {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DataAnonymizer", options);
    }    
}