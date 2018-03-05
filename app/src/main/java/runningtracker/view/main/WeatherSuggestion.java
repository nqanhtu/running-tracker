package runningtracker.view.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import runningtracker.R;
import runningtracker.model.modelrunning.DatabaseWeather;
import runningtracker.model.modelrunning.WeatherObject;

public class WeatherSuggestion  extends AppCompatActivity{
    private TextView txtName, txtDay, txtTemperature, txtSuggestion;
    private ImageView imgIcon, imgSuggestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        setupViewWeather();
        DatabaseWeather databaseWeather = new DatabaseWeather(this);
        WeatherObject weatherObject = new WeatherObject();
        ArrayList<WeatherObject> objectArrayList = new ArrayList<WeatherObject>();
        objectArrayList = databaseWeather.getAllWeather();
        setValueViewWeather(objectArrayList, weatherObject);
    }
    //ham set gia tri cho view
    private void setValueViewWeather(ArrayList<WeatherObject> objectArrayList,  WeatherObject weatherObject){
        if(objectArrayList.size() > 0) {
            weatherObject = objectArrayList.get(0);
            txtName.setText(weatherObject.getName());
            txtDay.setText(weatherObject.getDay());
            txtTemperature.setText(weatherObject.getTemp());
            Picasso.with(WeatherSuggestion.this).load("http://openweathermap.org/img/w/"+weatherObject.getIcon()+".png").into(imgIcon);
            txtSuggestion.setText(suggestionLocation(weatherObject));
        }
    }
    //ham khai bao bien cho view
    private void setupViewWeather(){
        txtName = (TextView) findViewById(R.id.txtName);
        txtDay = (TextView) findViewById(R.id.txtDay);
        txtTemperature = (TextView) findViewById(R.id.txtTemperature);
        txtSuggestion = (TextView) findViewById(R.id.txtSuggestion);
        imgIcon = (ImageView)  findViewById(R.id.imgIcon);
        imgSuggestion = (ImageView)  findViewById(R.id.imgSuggestion);
    }
    //Ham dua ra goi y thoi tiet
    private String suggestionLocation(WeatherObject weatherObject) {
        String str ="";
        switch(weatherObject.getIcon())
        {
            case "01d":
                str = "Trời quang đãng có thể tập luyện";
                break;
            case "02d" :
                str = "Trời có ít mây rất mát mẻ để tập luyện";
                break;
            case "03d" :
                str = "Trời có mây nhẹ rất thuận lợi cho việc tập luyện";
                break;
            case "04d" :
                str = "Trời có mây đen nhiều không thoải mái để tập luyện";
                break;
            case "09d" :
                str = "Trời có mưa không thích hợp  để tập luyện";
                break;
            case "10d" :
                str = "Trời có mưa không thích hợp  để tập luyện";
                break;
            case "11d" :
                str = "Trời có mưa kèm theo dông và sắm chớp rất nguy hiểm khi tập luyện";
                break;
            case "13d" :
                str = "Trời đang có tuyết không thích hợp để tập luyện";
                break;
            case "50d" :
                str = "Trời đang có sương mù không thích hợp để tập luyện";
                break;
            default :
                str = "Hiện tại là ban đêm không thích hợp để tập luyện";
                break;
        }
        return str;
    }

}
