package com.github.echo.util;

import org.apache.commons.cli.*;

/**
 * @author zouwei
 * @className CommandParser
 * @date: 2021/6/7 下午7:12
 * @description:
 */
public class CommandLineUtil {

    public static Options buildCommandLineOptions(Option... optionArray) {
        if (optionArray.length <= 0) {
            throw new IllegalArgumentException("options number is zero");
        }
        Options options = new Options();
        for (Option option : optionArray) {
            options.addOption(option);
        }
        return options;
    }

    public static CommandLine parse(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            return null;
        }
        return commandLine;
    }
}
