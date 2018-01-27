package runningtracker.model.modelrunning;



public class WeatherObject {
    private String name;
    private String main;
    private String description;
    private String icon;
    private String temp;
    private String day;

    public WeatherObject() {
    }

    public WeatherObject(String name, String main, String description, String icon, String temp, String day) {
        this.name = name;
        this.main = main;
        this.description = description;
        this.icon = icon;
        this.temp = temp;
        this.day = day;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
