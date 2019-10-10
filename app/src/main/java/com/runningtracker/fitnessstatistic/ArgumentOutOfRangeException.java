package com.runningtracker.fitnessstatistic;

public class ArgumentOutOfRangeException extends IllegalArgumentException {
	public final Number MIN;
	public final Number MAX;
	public final Number INPUT_VALUE;
	public final String ARGUMENT_NAME;
	
	public ArgumentOutOfRangeException(Number min, Number max, Number inputValue, String argumentName) {
		super("The input value " + inputValue + " of argument " + argumentName + " is out of range [" + min + ", " + max + "].");
		MIN = min;
		MAX = max;
		INPUT_VALUE = inputValue;
		ARGUMENT_NAME = argumentName;
	}
}