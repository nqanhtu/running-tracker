package runningtracker.model.modelrunning;

public class BodilyCharacteristicObject {
    private int age;
    private  String gender;
    private int weightInKg;
    private  int heightInCm;
    private int vo2Max;
    private  float restingMetabolicRate;
    public BodilyCharacteristicObject(){}

    public BodilyCharacteristicObject(int age, String gender, int weightInKg, int heightInCm, int VO2max, float restingMetabolicRate) {
        this.age = age;
        this.gender = gender;
        this.weightInKg = weightInKg;
        this.heightInCm = heightInCm;
        this.vo2Max = VO2max;
        this.restingMetabolicRate = restingMetabolicRate;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public float getWeightInKg() {return weightInKg;}

    public void setWeightInKg(int weightInKg) {
        this.weightInKg = weightInKg;
    }

    public int getHeightInCm() {
        return heightInCm;
    }

    public void setHeightInCm(int heightInCm) {
        this.heightInCm = heightInCm;
    }

    public int getVo2Max() {
        return vo2Max;
    }

    public void setVo2Max(int vo2Max) {
        this.vo2Max = vo2Max;
    }

    public float getRestingMetabolicRate() {
        return restingMetabolicRate;
    }

    public void setRestingMetabolicRate(float restingMetabolicRate) {
        this.restingMetabolicRate = restingMetabolicRate;
    }

}
