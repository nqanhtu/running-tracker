package runningtracker.Presenter.fitnessstatistic;


public abstract class Calculator {

	private static double maximumHeartRate(int testeeAge) {
		return 208 - 0.7 * testeeAge;
	}

	private static double cardiorespiratoryFitnessFactor(double testeeVO2max) {
		double result = 0;
		if (testeeVO2max < 44) {
			result = 1.07;
		}
		else if (testeeVO2max >= 44 && testeeVO2max < 46) {
			result = 1.06;
		}
		else if (testeeVO2max >= 46 && testeeVO2max < 48) {
			result = 1.05;
		}
		else if (testeeVO2max >= 48 && testeeVO2max < 50) {
			result = 1.04;
		}
		else if (testeeVO2max >= 50 && testeeVO2max < 52) {
			result = 1.03;
		}
		else if (testeeVO2max >= 52 && testeeVO2max < 54) {
			result = 1.02;
		}
		else if (testeeVO2max >= 54 && testeeVO2max < 56) {
			result = 1.01;
		}
		else {
			result = 1.00;
		}
		return result;
	}

	private static double gradientFactor(int roadGradient) throws ArgumentOutOfRangeException {
		double result = 0;
		if (roadGradient >= -20 && roadGradient <= -15) {
			result = -0.01 * roadGradient + 0.50;
		}
		else if (roadGradient > -15 && roadGradient <= -10) {
			result = -0.02 * roadGradient + 0.35;
		}
		else if (roadGradient > -10 && roadGradient <= 0) {
			result = 0.04 * roadGradient + 0.95;
		}
		else if (roadGradient > 0 && roadGradient <= 10) {
			result = 0.05 * roadGradient + 0.95;
		}
		else if (roadGradient > 10 && roadGradient <= 15) {
			result = 0.07 * roadGradient + 0.75;
		}
		else {
			// This exception is thrown when road gradient is lower than -20% or higher than 15%, which means it's impossible to run on it.
			throw new ArgumentOutOfRangeException(Integer.valueOf(-20), Integer.valueOf(15), Integer.valueOf(roadGradient), "roadGradient");
		}
		return result;
	}

	private static double treadmillFactor(boolean runOnTreadmill) {
		return runOnTreadmill ? 0 : 0.84;
	}

	// The amount of calorie which gets burned due to metabolism, i.e. constantly burned even if the body is idle.
	private static double restingMetabolicRateCalorieBurned(double runnerRestingMetabolicRate, double runningDurationInHour) {
		// Resting Metabolic Rate is the amount of calorie burned in a day due to metabolism.
		// So the below formula return the amount of metabolic calorie burned during running session.
		return (runnerRestingMetabolicRate / 24) * runningDurationInHour;
	}

	// The gross amount of calorie that the runner's body burns during the running session.
	public static double grossCalorieBurned(double netCalorieBurned, double runnerRestingMetabolicRate, double runningDurationInHour) {
		return netCalorieBurned + restingMetabolicRateCalorieBurned(runnerRestingMetabolicRate, runningDurationInHour);
	}
	
	// The amount of calorie burned due to the running activity.
	// roadGradient: [-20, 15], positive number means inclining road, otherwise declining.
	public static double netCalorieBurned(double runnerWeightInKg, double runnerVO2max, double distanceRanInKm, int roadGradient, boolean runOnTreadmill) {
		return (gradientFactor(roadGradient) * runnerWeightInKg + treadmillFactor(runOnTreadmill)) * distanceRanInKm * cardiorespiratoryFitnessFactor(runnerVO2max);
	}

	public static double vO2max(int testeeAge, int testeeRestingHeartRate) {
		// Resting Heart Rate is the number of heartbeats per minute.
		return 15.3 * maximumHeartRate(testeeAge) / testeeRestingHeartRate;
	}

	public static double restingMetabolicRate(int testeeAge, Gender testeeGender, double testeeWeightInKg, int testeeHeightInCm) {
		double result = 0;
		if (testeeGender == Gender.FEMALE) {
			result = (9.56 * testeeWeightInKg + 1.85 * testeeHeightInCm - 4.68 * testeeAge + 655 ) * 1.1;
		}
		else {
			result = (13.75 * testeeWeightInKg + 5 * testeeHeightInCm - 6.76 * testeeAge + 66) * 1.1;
		}
		return result;
	}
}