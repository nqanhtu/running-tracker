package runningtracker.settings;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

import com.google.firebase.firestore.FirebaseFirestore;

import runningtracker.R;
import runningtracker.common.InitializationFirebase;
import runningtracker.data.model.setting.ShareLocationObject;

public class SettingDashBoardActivity extends PreferenceActivity {

    SwitchPreference onOffShareLocation;
    private FirebaseFirestore firestore;
    private SettingDashBoardPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        onOffShareLocation = (SwitchPreference) findPreference("pre_sharelocation");
        /**
         * Create Presenter value
         * */
        presenter = new SettingDashBoardPresenter();
        /**
         * Create fire store
         * */
        InitializationFirebase initializationFirebase = new InitializationFirebase();
        firestore = initializationFirebase.createFirebase();
        /**
         * Run update share location
         * */
        updateShareLocation();
    }
    /**
     *Preference change event  update value share location of user
    * */
    void updateShareLocation(){
        onOffShareLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if(onOffShareLocation.isChecked()){

                    // Checked the switch programmatically
                    onOffShareLocation.setChecked(false);
                }else {

                    // Unchecked the switch programmatically
                    onOffShareLocation.setChecked(true);
                }

                Boolean statusShare = (Boolean)o;
                Boolean shareLocationObject;
                shareLocationObject = statusShare;
                presenter.updateValueShareLocation(firestore, shareLocationObject);

                return false;
            }
        });
    }

}
