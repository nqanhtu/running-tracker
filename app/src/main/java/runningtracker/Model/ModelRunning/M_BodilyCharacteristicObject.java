package runningtracker.model.modelrunning;


/**
 * Created by Minh Tri on 2017-10-12.
 */

public class M_BodilyCharacteristicObject {


    private int Age;
    private  String Gender;
    private int WeightInKg;
    private  int HeightInCm;
    private int VO2max;
    private  float RestingMetabolicRate;
    public M_BodilyCharacteristicObject(){}

    public M_BodilyCharacteristicObject(int age, String gender, int weightInKg, int heightInCm, int VO2max, float restingMetabolicRate) {
        Age = age;
        Gender = gender;
        WeightInKg = weightInKg;
        HeightInCm = heightInCm;
        this.VO2max = VO2max;
        RestingMetabolicRate = restingMetabolicRate;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public float getWeightInKg() {
        return WeightInKg;
    }

    public void setWeightInKg(int weightInKg) {
        WeightInKg = weightInKg;
    }

    public int getHeightInCm() {
        return HeightInCm;
    }

    public void setHeightInCm(int heightInCm) {
        HeightInCm = heightInCm;
    }

    public int getVO2max() {
        return VO2max;
    }

    public void setVO2max(int VO2max) {
        this.VO2max = VO2max;
    }

    public float getRestingMetabolicRate() {
        return RestingMetabolicRate;
    }

    public void setRestingMetabolicRate(float restingMetabolicRate) {
        RestingMetabolicRate = restingMetabolicRate;
    }

}
